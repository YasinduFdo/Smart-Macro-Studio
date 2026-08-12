package com.smartmacro.ui;

import com.smartmacro.datastructure.ActionLinkedList;
import com.smartmacro.datastructure.MacroQueue;
import com.smartmacro.editor.TimelineEditor;
import com.smartmacro.model.AutomationAction;
import com.smartmacro.recorder.GlobalHookRecorder;
import com.smartmacro.undoredo.UndoRedoManager;
import com.smartmacro.playback.PlaybackEngine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {

    private ActionLinkedList activeList;
    private UndoRedoManager undoRedo;
    private TimelineEditor editor;
    private GlobalHookRecorder recorder;
    private PlaybackEngine player;
    private MacroQueue macroQueue; 

    private Map<String, ActionLinkedList> macroStorage = new LinkedHashMap<>();
    private ListView<String> macroListView = new ListView<>();
    private ListView<String> queueListView = new ListView<>(); 

    private TableView<AutomationAction> table;
    private ObservableList<AutomationAction> tableData;

    @Override
    public void start(Stage primaryStage) {
        undoRedo = new UndoRedoManager();
        macroQueue = new MacroQueue();

        BorderPane root = new BorderPane();
        
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        leftPanel.setPrefWidth(180);
        
        Label lblMacros = new Label("Saved Macros:");
        lblMacros.setStyle("-fx-font-weight: bold;");
        
        Button btnNewMacro = new Button("➕ New Macro");
        btnNewMacro.setMaxWidth(Double.MAX_VALUE);
        btnNewMacro.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnNewMacro.setOnAction(e -> createNewMacro());

        Button btnDeleteMacro = new Button("🗑 Delete Macro");
        btnDeleteMacro.setMaxWidth(Double.MAX_VALUE);
        btnDeleteMacro.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDeleteMacro.setOnAction(e -> deleteSelectedMacro());

        macroListView.setPrefHeight(130);
        macroListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switchMacro(newVal);
            }
        });

        Separator sep = new Separator();
        
        Label lblQueue = new Label("Execution Queue (FIFO):");
        lblQueue.setStyle("-fx-font-weight: bold;");
        
        Button btnEnqueue = new Button("⬇️ Enqueue Selected");
        btnEnqueue.setMaxWidth(Double.MAX_VALUE);
        
        queueListView.setPrefHeight(120);
        
        Button btnExecuteQueue = new Button("▶ Execute Queue");
        btnExecuteQueue.setMaxWidth(Double.MAX_VALUE);
        btnExecuteQueue.setStyle("-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-font-weight: bold;");

        leftPanel.getChildren().addAll(
            lblMacros, btnNewMacro, btnDeleteMacro, macroListView, 
            sep, 
            lblQueue, btnEnqueue, queueListView, btnExecuteQueue
        );
        root.setLeft(leftPanel);

        // --- TOP TOOLBAR ---
        HBox toolbar = new HBox(15);
        toolbar.setPadding(new Insets(15));
        toolbar.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        Button btnStart = new Button("⏺ Record");
        Button btnStop = new Button("⏹ Stop");
        Button btnPlay = new Button("▶ Play Single Macro");
        Button btnDelete = new Button("🗑 Delete Action"); 
        Button btnUndo = new Button("↩ Undo");
        Button btnRedo = new Button("↪ Redo");

        btnStart.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        btnStop.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPlay.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDelete.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;"); 
        
        toolbar.getChildren().addAll(btnStart, btnStop, new Separator(), btnPlay, btnDelete, new Separator(), btnUndo, btnRedo);
        root.setTop(toolbar);

        // --- CENTER TABLE ---
        table = new TableView<>();
        tableData = FXCollections.observableArrayList();
        table.setItems(tableData);

        TableColumn<AutomationAction, String> typeCol = new TableColumn<>("Action Type");
        typeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActionType().name()));
        typeCol.setPrefWidth(150);

        TableColumn<AutomationAction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().describe()));
        descCol.setPrefWidth(450);

        TableColumn<AutomationAction, String> delayCol = new TableColumn<>("Delay (ms)");
        delayCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getDelay())));
        delayCol.setPrefWidth(100);

        table.getColumns().addAll(typeCol, descCol, delayCol);
        root.setCenter(table);

        // --- BUTTON ACTIONS ---
        btnStart.setOnAction(e -> { 
            if (recorder != null) {
                recorder.start(); 
                Platform.runLater(() -> {
                    btnStart.setText("🔴 Recording...");
                    btnStart.setStyle("-fx-background-color: #b71c1c; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #ff5252; -fx-border-width: 2px;");
                    btnStart.setDisable(true); 
                });
            }
        });

        btnStop.setOnAction(e -> { 
            if (recorder != null) {
                recorder.stop(); 
                Platform.runLater(() -> {
                    btnStart.setText("⏺ Record");
                    btnStart.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                    btnStart.setDisable(false); 
                    refreshTable();
                });
            }
        });

        btnDelete.setOnAction(e -> {
            int selectedIndex = table.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && editor != null) {
                editor.delete(selectedIndex + 1); 
                refreshTable();
            }
        });

        btnUndo.setOnAction(e -> { 
            if (editor != null) {
                editor.undo(); 
                refreshTable(); 
            }
        });

        btnRedo.setOnAction(e -> { 
            if (editor != null) {
                editor.redo(); 
                refreshTable(); 
            }
        });

        btnPlay.setOnAction(e -> {
            if (player != null) {
                new Thread(() -> player.play()).start();
            }
        });

        // --- QUEUE ACTIONS ---
        btnEnqueue.setOnAction(e -> {
            String selectedName = macroListView.getSelectionModel().getSelectedItem();
            if (selectedName != null) {
                ActionLinkedList listToQueue = macroStorage.get(selectedName);
                macroQueue.enqueue(selectedName, listToQueue);
                queueListView.getItems().add(selectedName);
            }
        });

        btnExecuteQueue.setOnAction(e -> {
            new Thread(() -> {
                System.out.println("Starting Queue Execution...");
                while (!macroQueue.isEmpty()) {
                    ActionLinkedList currentMacro = macroQueue.dequeue();
                    
                    if (currentMacro == null) break;

                    Platform.runLater(() -> {
                        if (!queueListView.getItems().isEmpty()) {
                            queueListView.getItems().remove(0);
                        }
                    });

                    PlaybackEngine tempPlayer = new PlaybackEngine(currentMacro);
                    tempPlayer.play();
                    
                    try { Thread.sleep(500); } catch (InterruptedException ex) {}
                }
                System.out.println("Queue Execution Complete!");
            }).start();
        });

        createNewMacro();

        // --- WINDOW SETUP ---
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Smart Macro Studio");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(e -> {
            if (recorder != null) recorder.stop();
            Platform.exit();
            System.exit(0);
        });
    }

    private void createNewMacro() {
        String name = "Macro " + (macroStorage.size() + 1);
        ActionLinkedList newList = new ActionLinkedList();
        macroStorage.put(name, newList);
        
        macroListView.getItems().add(name);
        macroListView.getSelectionModel().select(name);
    }

    private void deleteSelectedMacro() {
        String selectedName = macroListView.getSelectionModel().getSelectedItem();
        if (selectedName == null) return;

        if (macroStorage.size() <= 1) {
            System.out.println("Cannot delete the last remaining macro.");
            return;
        }

        macroStorage.remove(selectedName);
        macroListView.getItems().remove(selectedName);

        String nextMacro = macroListView.getItems().get(0);
        macroListView.getSelectionModel().select(nextMacro);
    }

    private void switchMacro(String macroName) {
        activeList = macroStorage.get(macroName);
        if (activeList == null) return;
        
        if (recorder == null) {
            recorder = new GlobalHookRecorder(activeList);
        } else {
            recorder.setList(activeList);
        }
        
        player = new PlaybackEngine(activeList);
        editor = new TimelineEditor(activeList, undoRedo);
        refreshTable();
    }

    private void refreshTable() {
        tableData.clear();
        if (activeList != null) {
            List<AutomationAction> actions = activeList.forwardTraversal();
            tableData.addAll(actions);
        }
        table.refresh(); 
    }

    public static void main(String[] args) {
        launch(args);
    }
}