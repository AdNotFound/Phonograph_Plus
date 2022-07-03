# Phonograph Plus

[![Crowdin](https://badges.crowdin.net/phonograph-plus/localized.svg)](https://crowdin.com/project/phonograph-plus)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://github.com/chr56/Phonograph_Plus/blob/release/LICENSE.txt)
[<img src="https://github.com/chr56/Phonograph/workflows/ci/badge.svg" alt="CI Status">](https://github.com/chr56/Phonograph_Plus/actions/workflows/ci.yml)

**Phonograph 第三方维护版**

**A fork of Phonograph under maintenance and development**


A material designed local music player for Android.

<br/>

This is a fork of [Phonograph](https://github.com/kabouzeid/Phonograph), with some extra additional features.

## **特性** / **Features**

建议直接看[更新日志](app/src/main/assets/phonograph-changelog-zh-rCN.html)!

It is suggested to browser the [Changelog](app/src/main/assets/phonograph-changelog.html) to learn all of features completely.

* 解锁 Pro | Unlock pro.

* 自动夜间模式 | Automatic & adaptive dark mode.

* 更改界面 | Many changes to UI.

* 详情对话框内显示Tag信息 | Show tag information in "Detail" Dialog

* 歌词对话框内显示歌词时间轴信息, 并可以通过长按进行快速转跳与自动滚动 | Show Time Axis in "Lyrics" Dialog and allow seeking basing
  lyric's time axis and support lyrics following.

* 适配 Android 11 分区存储 （部分） | Fix Android 11 Scope Storage.(WIP)

* 适当折叠歌曲弹出菜单 | Optimise song item menu.

* 改进媒体库交互 | Improve “Library” pages user experience。

* 增大“最近播放”和“最喜爱的歌曲(实际是“最常播放”的歌曲)”条目数量(100→150) | Increase history played tracks and top played
  tracks entries capacity (100->150).

* 新增崩溃报告页面 | Handle app crash.

* 支持更多排序方式 | Support more sort orders.

* 在歌曲(或文件)弹出菜单中, 快速添加黑名单 | Add song menu shortcut to add new items to blacklist.

* 适配" [墨·状态栏歌词](https://github.com/Block-Network/StatusBarLyric) "Xposed模块 | Co-work-with/Support
  StatusBar Lyric Xposed Module (api)
  
* 支持导出内部数据库以供备份 | Export internal databases for the need of backup.

* 允许标签固定并平铺 | Allow tabs fixed.

* 更新对话框样式 | Update dialogs style.

* 以及更多细小特性 | and more small features/fixes.

## **翻译**/**Translation**

Translate Phonograph Plus into your language -> [crowdin](https://crowdin.com/project/phonograph-plus)

## **截图**/**Screenshot**

仅供参考， 以实际为准

For reference only, actual app might be different

| Card Player | Flat Player |  Song Menu |
| :---------: | :---------: | :--------: |
| ![Screenshots](./art/05.jpg?raw=true) |![Screenshots](./art/08.jpg?raw=true) | ![Screenshots](./art/02.jpg?raw=true) 


Songs | Folders | Artists | Playlists |
:---: | :-----: | :-----: | :-------: |
| ![Screenshots](./art/09.jpg?raw=true) | ![Screenshots](./art/10.jpg?raw=true) | ![Screenshots](./art/07.jpg?raw=true) | ![Screenshots](./art/06.jpg?raw=true)|

| Drawer | Setting | Tag Editor (Deprecated) |
| :----: |:------: | :---------------------: |
| ![Screenshots](./art/03.jpg?raw=true)| ![Screenshots](./art/01.jpg?raw=true) | ![Screenshots](./art/04.jpg?raw=true) |



## **开发计划**/**Development Plan (or Road Map?)**
<br/>

**Phonograph Plus** is (partially) migrating to 🚀 Jetpack Compose -> see Branch [Compose](https://github.com/chr56/Phonograph_Plus/tree/Compose)

**Phonograph Plus** 正在（部分）迁移至 🚀 Jetpack Compose -> 参见 [Compose](https://github.com/chr56/Phonograph_Plus/tree/Compose)

<br/>

## **TO-DO list**

**2022**

- [x] 重构文件视图 | Refactor File Fragment

- [x] 重构媒体库UI | Refactor Library UI 

- [x] 实现更好的播放频率计数 | Better 'My Top Songs' algorithm

- [x] 完成 Readme | Complete README

- [ ] 将歌曲“详情” 迁移至 Compose ❗WIP (基本完成) | Migrate Song Detail to Jetpack Compose (❗WIP: Almost Done)

- [ ] 支持白名单机制 | Whitelist

- [ ] 自定义歌曲点击行为 | User-defined click behavior for songs

- [ ] 重构后台音乐服务 (❗WIP) | Refactor MusicService (❗WIP)
  
- [ ] 重构歌曲封面加载Glide模块 | Refactor Glide Module

- [ ] 重构更新对话框 | Refactor Update Dialog

- [ ] 重构设置UI | Refactor Setting UI
  
- [ ] 尝试适配 FlyMe / EvolutionX(等一系类原生)状态栏歌词 | Support some ROM's StatusBar lyrics, such as FlyMe / EvolutionX

- [ ] 自建本地媒体数据库(使用 AndroidX Room) ⭕, 以解析多艺术家歌曲, 并解析 Tag 中 ‘;’, '&', '/', '\', ',' , 改进搜索 | Use AndroidX Room to build Media database, to parse multi-artists songs and ‘;’, '&', '/', '\', ',' in tags,  and improve search result

- [ ]  ...

**2023~2024(?)**

- [ ] 增强“播放列表详情”(支持搜索 ❌, 更好的修改本地列表方式 ❗WIP, 响应打开文件的Intent ❌) | Enhance Playlist Detail: support search ❌, Better way to modify ❗WIP, handle intent of open (playlist) file ❌

- [ ] 检查文件 | Valid files

- [ ] 桌面歌词(?) | Desktop lyrics (?) 

- [ ] 改进 SlidingMusicBar | improve SlidingMusicBar

- [ ] 重写音乐标签编辑 | Rewrite Tag Editor

- [ ] <del>完美适配 Android11+ 的文件访问(❌) | Adapter Android11+ File Permission perfectly</del>

- [ ] <del>部分重构(所谓的)"主题引擎" | Refactor so-called Theme Engine</del>

- [ ] <del>统计听歌频率 | Make songs listening statistics</del>

- [ ] ...

<br/>
<br/>
<br/>
<br/>

