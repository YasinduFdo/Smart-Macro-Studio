# Smart Macro Automation Studio

Smart Macro Automation Studio is a Java and JavaFX desktop application that helps users automate repeated computer tasks.

The application can record mouse movements, mouse clicks, keyboard actions, and the time between actions. Recorded actions can then be edited, saved, scheduled, and played back automatically.

This project was developed as part of the Programming Data Structures and Algorithms (PDSA) coursework at the National Institute of Business Management (NIBM).

---

## Features

### Macro Recording
- Record mouse movements
- Record mouse clicks
- Record keyboard inputs
- Record the time delay between actions

### Timeline Editing
- Insert actions
- Delete actions
- Reorder actions
- Duplicate actions
- Disable actions
- Undo and Redo

### Custom Doubly Linked List
The recorded actions are stored using a manually implemented Doubly Linked List.

Each node contains:
- Automation Action
- Previous node reference
- Next node reference

### Macro Queue
A Queue is used to manage multiple macros.

Example:

Macro A → Macro B → Macro C

The macros are executed using the FIFO (First In, First Out) method.

### Smart Regex Processing
The Regex Processing Engine can identify selected text patterns and replace fixed values with dynamic values.

For example:

Recorded date → Dynamic current date

### PowerShell Script Generation
Recorded actions can be converted into PowerShell commands and exported as a `.ps1` file.

### Statistics Dashboard
The application provides JavaFX charts to display information such as:
- Total actions
- Recording time
- Macro count
- Dynamic variable usage

---

## Technologies Used

- Java
- JavaFX
- Object-Oriented Programming (OOP)
- Custom Doubly Linked List
- Queue
- Regular Expressions (Regex)
- PowerShell
- JSON

---

## Data Structures

### Doubly Linked List

The Doubly Linked List is the main data structure used in the project.

It stores the recorded actions in order and allows the application to move both forward and backward through the timeline.

It is useful for:
- Inserting actions
- Deleting actions
- Reordering actions
- Timeline navigation
- Undo and Redo

### Queue

A Queue is used for macro scheduling.

The system follows the FIFO principle:

First Macro Added → First Macro Executed

---

## System Flow

User Action
↓
Global Action Recorder
↓
Automation Action
↓
Doubly Linked List
↓
Timeline Editor
↓
Playback Engine
↓
Automated Desktop Actions

Multiple saved macros can also be sent through the Macro Queue for batch execution.

---

## Novel Features

### 1. Smart Regex Processing Engine

The system can identify selected static text and replace it with dynamic information during playback.

### 2. PowerShell Script Generation

The recorded actions can be converted into PowerShell commands and exported as a `.ps1` file.

### 3. Macro Queue

Multiple macros can be placed into a Queue and executed one after another.

---

## Project Structure

```text
Smart-Macro-Automation-Studio/
│
├── src/
│   └── ...
│
├── resources/
│   └── ...
│
├── screenshots/
│   └── ...
│
├── README.md
├── .gitignore
└── pom.xml
