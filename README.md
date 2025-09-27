# UmaFX

UmaFX is a simple JavaFX desktop jukebox with a cute, draggable sprite companion. It lets you queue and play local music
files.

## Features

- Draggable transparent window with an animated character sprite
- Song queue dialog to add files or entire folders (MP3, WAV)
- Playback controls:
    - Play / Pause, Stop, Skip
    - Context menu on the jukebox image
    - System tray menu with quick controls

## Download
Grab the latest installer from the [releases](https://github.com/SundenJaeger/UmaFX/releases) tab.

### Requirements
- OS: **Windows 10** or **Windows 11**
- Architecture: **x64**

## Usage

- Drag the character by left-clicking and holding to move the window.
- Left-click the jukebox to open the Song Queue:
    - Add Folder: imports all .mp3 and .wav files in the selected folder
    - Add Song: import multiple music files
    - Select items and press Delete or use the context menu to remove
- Right-click the character to switch characters
- Right-click the jukebox for the context menu:
    - Open Song Queue
    - Play/Pause, Stop, Skip
    - Exit
- System tray icon:
    - Reset Window
    - Open Song Queue
    - Play/Pause, Stop, Skip
    - Exit

## Configuration

Configuration files and spritesheet templates are located in your **Documents/UmaFX** folder.

### Config.properties

You can change starting character by changing `Character` value

```
Character=<character folder name>
Version=1
```

- `Character` - Name of the character's folder
- `Version` - Internal version number **(DO NOT CHANGE THIS)**

### Creating custom spritesheets

Inside the `spritesheets` folder, you will find a `teio` folder. <br>
This serves as the **base template** if you want to create your own spritesheets.

When making a new character folder, make sure it includes a `config.json` file

### Config.json

Each character's animations are defined in `config.json`.  <br>
There are three supported animation states:

- `idle`
- `ready`
- `dance`

You can also define multiple variants of an animation state by appending an underscore and a number:
(e.g., `dance_01`, `dance_02`, `dance_03`, …).
The program will recognize all animations with the same base name.

**Example:**

```
"name": "dance_01",
"file": "teio_outfit_01_dance_01.png",
"columns": 8,
"rows": 12,
"totalFrames": 90,
"frameWidth": 375,
"frameHeight": 375,
"loop": true
```

- `name` - Animation state (`idle`, `ready`, `dance` or numbered variant (`dance_01`, `dance_02`, …)
- `file` - Filename of the spritesheet image (must be inside the same folder)
- `columns` - Number of columns in the spritesheet
- `rows` - Number of rows in the spritesheet
- `totalFrames` - Total number of animation frames.
- `frameWidth` - Width of each frame (pixels).
- `frameHeight` - Height of each frame (pixels).
- `loop` - Whether the animation should repeat (`true`/`false`)

## Contributing

- Fork the repo
- Create a feature branch
- Commit with clear messages
- Open a PR with a concise description and screenshots/gifs if UI is affected

## Third-party libraries used in this project

- [**FXTrayIcon**](https://github.com/dustinkredmond/FXTrayIcon) by Dustin Redmond
  under [**MIT License**](https://github.com/dustinkredmond/FXTrayIcon/blob/main/LICENSE)

## Gallery
<img width="1600" height="859" alt="1" src="https://github.com/user-attachments/assets/1fd58b6d-96c0-4e96-a90f-fb6cd7c5d6a3" />
<img width="168" height="168" alt="2" src="https://github.com/user-attachments/assets/d1e64ff4-209a-44b3-9902-3f0c51dd5416" />

