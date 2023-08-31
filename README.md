# Phonograph Plus

[![Crowdin](https://badges.crowdin.net/phonograph-plus/localized.svg)](https://crowdin.com/project/phonograph-plus)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://github.com/chr56/Phonograph_Plus/blob/release/LICENSE.txt)
[<img src="https://github.com/chr56/Phonograph_Plus/actions/workflows/dev.yml/badge.svg" alt="Dev CI Status">](https://github.com/chr56/Phonograph_Plus/actions/workflows/dev.yml)
![DownloadsStatistics](https://img.shields.io/github/downloads/chr56/Phonograph_Plus/total)

- [ENGLISH](./README.md)
- [简体中文](./README_ZH.md)

<br/>

**An independent fork of Phonograph under maintenance and development**

Phonograph is a material designed local music player for Android. And Phonograph Plus is an _independent_ fork
of [Phonograph](https://github.com/kabouzeid/Phonograph), and is currently under my maintenance and development.

## **Downloads**

[<img src="https://img.shields.io/github/v/release/chr56/phonograph_plus?label=Github%20Release" alt="Github%20Release">](https://github.com/chr56/Phonograph_Plus/releases/latest)
[<img src="https://img.shields.io/github/v/release/chr56/phonograph_plus?label=Github%20Release%20(Latest)&include_prereleases" alt="Github%20Release%20(Latest)">](https://github.com/chr56/Phonograph_Plus/releases/)
[<img src="https://img.shields.io/github/v/release/chr56/phonograph_plus?label=F-droid" alt="F-droid">](https://f-droid.org/packages/player.phonograph.plus/)

## **Additional Features**

All listed features are compared with the original Phonograph.

- Unlock pro.

- Automatic & adaptive dark mode.

- Plenty of user interface changes.

- Change language in application manually.

- Better Notification Image Quality for Android T and above.

- User-defined click behavior for songs.

- Support history of playing queue.

- Brand-new Detail page with more information like tags.

- Brand-new Tag Editor supporting more tags.

- Show Time Axis in "Lyrics" Dialog and allow seeking basing lyric's time axis and support lyrics following.

- Playback speed control.

- Basic support for Android Auto.

- Support more sort orders.

- Fix Android 11 Scope Storage. (Partial)

- Improve menu experience.

- Improve "Library" pages user experience.

- Allow collecting app crash report.

- use Path filter to replace, support "exclude mode" (blacklist) and "include mode" (whitelist).

- Add song menu shortcut to add new items to excluded-list (blacklist).

- Co-work-with/Support [StatusBar Lyric Xposed Module (api)](https://github.com/Block-Network/StatusBarLyric)

- Increase history played tracks and top played tracks entries capacity (100->150).

- Export internal databases and settings for the need of backup.

- Allow deleting songs file along with its external lyrics file.

- Allow tabs fixed.

- Brand-new File tab.

- Brand-new App Intro.

- Better Playlist support.

- <del>Poor</del> support of Monet.

- Resume playing after interruptions.

- Enhance multi-selecting.

- and more small features/fixes.

It is suggested to browser the [Changelog](https://phonographplus.github.io/changelogs/changeslogs/changelog.html) to
view all features completely

## **Translation**

Translate Phonograph Plus into your language -> [Crowdin](https://crowdin.com/project/phonograph-plus)

We have removed Swedish and Norwegian Nynorsk translations due to missing too many translations

## **Screenshot**

For reference only, actual app might be different

|                                           Card Player                                           |                                           Flat Player                                           |                                           Drawer                                            |
|:-----------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------:|
| ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/CardPlayer.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/FlatPlayer.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/Drawer.jpg?raw=true) |

|                                           Drawer                                            |                                             Songs                                             |                                             Files                                             |
|:-------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------:|
| ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/Drawer.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/SongPage.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/FilePage.jpg?raw=true) |

|                                             Artists                                             |                                             Albums                                             |                                             Playlists                                             |
|:-----------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
| ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/ArtistPage.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/AlbumPage.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/PlaylistPage.jpg?raw=true) |

|                                           Setting                                            |                                           Song Detail                                           |                                           Tag Editor                                           |
|:--------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------:|
| ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/Setting.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/SongDetail.jpg?raw=true) | ![Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/TagEditor.jpg?raw=true) |

## **Build Instructions & Developer Guide**

See [Build_Instructions.md](docs/Build_Instructions.md)

## **Development Plan (or Road Map?)** & **TO-DO list**

### **2022**

- [x] Refactor File Fragment

- [x] Refactor Library UI

- [x] Better 'My Top Songs' algorithm

- [x] Complete README

- [x] Refactor MusicService

- [x] Migrate Glide to Coil

- [x] Whitelist

- [x] Migrate Song Detail to Jetpack Compose

- [x] Refactor Update Dialog

- [x] User-defined click behavior for songs

- [x] Support history of playing queue.

- [x] Refactor Setting UI

- [x] Refactor Search

- [ ] Use AndroidX Room to build
  Media database, to parse multi-artists songs and ‘;’, '&', '/', '\', ',' in tags, and improve search result

### **2023~2024(?)**

- [x] Rewrite Tag Editor

- [x] Improve data backup/migrate

- [x] Refactor Pages

- [ ] Improve App Intro (WIP🚧)

- [ ] Refactor Main Player

- [ ] Enhance Playlist Detail: support search, Better way to
  modify , handle intent of open (playlist) file

- [ ] improve SlidingMusicBar

- [ ] Support some Android's StatusBar lyrics, such as FlyMe / EvolutionX

- [ ] <del>Validate audio files</del>

- [ ] <del>Completed Android 11+ File Permission</del>

- [ ] <del>Refactor so-called Theme Engine</del>

- [ ] <del>Make songs listening statistics</del>

- [ ] ...

## **Repository Mirror**

[![GitHub](https://img.shields.io/badge/Git-Github-Blue)](https://github.com/chr56/Phonograph_Plus/)
[![Codeberg](https://img.shields.io/badge/Git-Codeberg-Blue)](https://codeberg.org/PhonographPlus/Phonograph_Plus)
[![BitBucket](https://img.shields.io/badge/Git-BitBucket-Blue)](https://bitbucket.org/phonograph-plus/phonograph_plus/)
