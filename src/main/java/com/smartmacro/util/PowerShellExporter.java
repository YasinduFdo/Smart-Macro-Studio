package com.smartmacro.util;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.model.AutomationAction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PowerShellExporter {

    public static void exportToPs1(ActionLinkedList list, String filePath) throws IOException {
        StringBuilder script = new StringBuilder();

        // 1. Inject C# Native Methods for OS-level Mouse, Window, AND Keyboard Control
        script.append("Add-Type -AssemblyName System.Windows.Forms\n");
        script.append("Add-Type -TypeDefinition @\"\n");
        script.append("using System;\n");
        script.append("using System.Runtime.InteropServices;\n");
        script.append("public class NativeInput {\n");
        script.append("    [DllImport(\"user32.dll\")]\n");
        script.append("    public static extern void mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);\n");
        script.append("    [DllImport(\"user32.dll\")]\n");
        script.append("    public static extern bool SetCursorPos(int x, int y);\n");
        script.append("    [DllImport(\"kernel32.dll\")]\n");
        script.append("    public static extern IntPtr GetConsoleWindow();\n");
        script.append("    [DllImport(\"user32.dll\")]\n");
        script.append("    public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);\n");
        
        // NEW: Native keyboard event specifically for the Windows key
        script.append("    [DllImport(\"user32.dll\")]\n");
        script.append("    public static extern void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);\n");
        script.append("}\n");
        script.append("\"@\n\n");
        
        script.append("Write-Host 'Starting Smart Macro Execution...' -ForegroundColor Green\n");
        
        // Minimize the PowerShell console window
        script.append("# Minimize the PowerShell console window\n");
        script.append("$hwnd = [NativeInput]::GetConsoleWindow()\n");
        script.append("[NativeInput]::ShowWindow($hwnd, 6) | Out-Null # 6 = SW_MINIMIZE\n");
        
        // Give the OS a tiny fraction of a second to complete the minimize animation before clicking starts
        script.append("Start-Sleep -Milliseconds 600\n\n");

        List<AutomationAction> actions = list.forwardTraversal();

        for (AutomationAction action : actions) {
            if (!action.isEnabled()) continue;
            
            // Dynamic variable replacement for timeline delays
            if (action.getDelay() > 0) {
                script.append("Start-Sleep -Milliseconds ").append(action.getDelay()).append("\n");
            }

            switch (action.getActionType()) {
                case MOUSE_MOVE:
                    script.append(String.format("[NativeInput]::SetCursorPos(%d, %d)\n", action.getMouseX(), action.getMouseY()));
                    break;
                case LEFT_CLICK:
                    script.append("[NativeInput]::mouse_event(0x02, 0, 0, 0, 0) # Mouse Left Down\n");
                    script.append("[NativeInput]::mouse_event(0x04, 0, 0, 0, 0) # Mouse Left Up\n");
                    break;
                case RIGHT_CLICK:
                    script.append("[NativeInput]::mouse_event(0x08, 0, 0, 0, 0) # Mouse Right Down\n");
                    script.append("[NativeInput]::mouse_event(0x10, 0, 0, 0, 0) # Mouse Right Up\n");
                    break;
                case MIDDLE_CLICK:
                    script.append("[NativeInput]::mouse_event(0x20, 0, 0, 0, 0) # Mouse Middle Down\n");
                    script.append("[NativeInput]::mouse_event(0x40, 0, 0, 0, 0) # Mouse Middle Up\n");
                    break;
                
                // NEW: Handle all recorded keystrokes from your GlobalHookRecorder
                case KEY_DOWN: 
                    String key = action.getKeyValue();
                    if ("WINDOWS".equalsIgnoreCase(key)) {
                        // 0x5B is the exact hardware code for the Windows Key
                        script.append("[NativeInput]::keybd_event(0x5B, 0, 0, 0) # Win Key Down\n");
                        script.append("[NativeInput]::keybd_event(0x5B, 0, 0x0002, 0) # Win Key Up\n");
                        
                        // Critical delay: Give Windows 500ms to open the start menu before we start typing
                        script.append("Start-Sleep -Milliseconds 500\n"); 
                    } else {
                        String psKey = mapToSendKeys(key);
                        if (psKey != null) {
                            script.append(String.format("[System.Windows.Forms.SendKeys]::SendWait('%s')\n", psKey));
                        }
                    }
                    break;
                
                case KEY_UP:
                    // Ignored because SendWait automatically handles both key press and key release
                    break;
                    
                case TYPE_TEXT:
                    String escapedText = escapeForSendKeys(action.getText());
                    script.append(String.format("[System.Windows.Forms.SendKeys]::SendWait('%s')\n", escapedText));
                    break;
                    
                default:
                    break;
            }
        }

        script.append("\nWrite-Host 'Execution Complete.' -ForegroundColor Green\n");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(script.toString());
        }
    }

    /**
     * Maps standard key names to PowerShell SendKeys format
     */
    private static String mapToSendKeys(String key) {
        if (key == null) return null;
        key = key.toUpperCase();
        switch (key) {
            case "ENTER": return "{ENTER}";
            case "TAB": return "{TAB}";
            case "ESCAPE": case "ESC": return "{ESC}";
            case "BACKSPACE": return "{BACKSPACE}";
            case "SPACE": return " ";
            case "UP": return "{UP}";
            case "DOWN": return "{DOWN}";
            case "LEFT": return "{LEFT}";
            case "RIGHT": return "{RIGHT}";
            case "DELETE": case "DEL": return "{DELETE}";
            case "SHIFT": case "CONTROL": case "CTRL": case "ALT": 
                return null; // Skip modifier holds for basic typing to avoid stuck keys
            default:
                if (key.length() == 1) {
                    char c = key.toLowerCase().charAt(0); 
                    if ("+^%~(){}[]".indexOf(c) >= 0) {
                        return "{" + c + "}";
                    } else if (c == '\'') {
                        return "''";
                    }
                    return String.valueOf(c);
                }
                return null;
        }
    }

    private static String escapeForSendKeys(String text) {
        if (text == null) return "";
        StringBuilder escaped = new StringBuilder();
        for (char c : text.toCharArray()) {
            if ("+^%~(){}[]".indexOf(c) >= 0) {
                escaped.append("{").append(c).append("}");
            } else if (c == '\'') {
                escaped.append("''"); 
            } else {
                escaped.append(c);
            }
        }
        return escaped.toString();
    }
}