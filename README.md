[![Jitpack](https://jitpack.io/v/MajidArabi/AndroidFilePicker.svg)](https://jitpack.io/#MajidArabi/AndroidFilePicker)
[![Downloads](https://jitpack.io/v/MajidArabi/androidfilepicker/month.svg)](https://jitpack.io/#MajidArabi/androidfilepicker)
[![Stars](https://img.shields.io/github/stars/MajidArabi/AndroidFilePicker)](https://github.com/MajidArabi/AndroidFilePicker/stargazers)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Latestver](https://lv.binarybabel.org/catalog-api/gradle/latest.svg?v=7.2)](https://lv.binarybabel.org/catalog/gradle/latest)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Simple, Powerful and Beautiful Android Image/Video/Audio Picker 😎
**Features** 😍
 - No need check storage permission 😉
 - Single and multiple selection
 - Supported RTL and LTR list direction (default=LTR)
 - Supported image, video or audio (default=image)
 - Supported custom title
 - Supported custom colors
 - Supported dynamic span count (default=2)
 - Limit max item selection (default=1)
 - Show file directory
 - Show file size

## Screenshots

| Image | Video | Audio
|--|--|--|
| <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/image.png" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/video.png" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/audio.png" width="250" />

## Download

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.majidarabi:AndroidFilePicker:$LATEST_VERSION'
	}

## Usage

**Kotlin**

	showFilePicker(
        limitItemSelection = 5,
        listDirection = ListDirection.RTL,
        accentColor = ContextCompat.getColor(this@MainActivity, R.color.purple_700),
        titleTextColor = ContextCompat.getColor(this@MainActivity, R.color.purple_700),
        onSubmitClickListener = object : OnSubmitClickListener {
            override fun onClick(files: List<Media>) {
                // Do something here with selected files
            }
        },
        onItemClickListener = object : OnItemClickListener {
            override fun onClick(media: Media, position: Int, adapter: ItemAdapter) {
                if (!media.file.isDirectory) {
                    adapter.setSelected(position)
                }
            }
        }
    )
	
**Java**
	
	new FilePicker.Builder(this)
                .setLimitItemSelection(3)
                .setAccentColor(Color.CYAN)
                .setCancellable(false)
                .setOnSubmitClickListener(files -> {
                    // Do something here with selected files
                })
                .setOnItemClickListener((media, pos, adapter) -> {
                    if (!media.getFile().isDirectory()) {
                        adapter.setSelected(pos);
                    }
                })
                .buildAndShow();

## Author

**Majid Arabi**
- [Github](https://github.com/MajidArabi)
- [Linkedin](https://www.linkedin.com/in/majid-arabi-673855129/)
- [Telegram](https://t.me/one_developer)
- [Site](http://one-developer.ir)
- [Email](mailto:majidarabi73@gmail.com)
