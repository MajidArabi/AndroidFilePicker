[![](https://jitpack.io/v/MajidArabi/AndroidFilePicker.svg)](https://jitpack.io/#MajidArabi/AndroidFilePicker) ![](https://img.shields.io/github/issues/MajidArabi/AndroidFilePicker) ![](https://img.shields.io/github/forks/MajidArabi/AndroidFilePicker) ![](https://img.shields.io/github/stars/MajidArabi/AndroidFilePicker)  ![](https://img.shields.io/badge/minSdk-21-yellowgreen)

## Simple, Powerful and Beautiful Android Image or Video Picker 😎
**Features** 😍
 - No need check storage permission 😉
 - Single and multiple selection
 - Supported RTL and LTR list direction (default=LTR)
 - Supported image and video (default=image)
 - Supported custom title
 - Supported custom colors
 - Supported dynamic span count (default=2)
 - Limit max item selection (default=1)
 - Show file directory
 - Show file size

## Screenshots

| Image | Image | Video
|--|--|--|
| <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/image-2col-full.jpg" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/image-3col.jpg" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/video-2col-full.jpg" width="250" />

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

## Usage (Just Kotlin)
	showFilePicker(
        gridSpanCount = 3,
        limitItemSelection = 5,
        listDirection = ListDirection.RTL,
        fileType = if (video) FileType.VIDEO else FileType.IMAGE,
        titleTextColor = ContextCompat.getColor(this, R.color.black),
        submitTextColor = ContextCompat.getColor(this, R.color.white),
        accentColor = ContextCompat.getColor(this, R.color.purple_200),
	) { 
		// Do something here with selected files
	}

## Author

**Majid Arabi**
- Github: [@majidarabi](https://github.com/MajidArabi)
- Linkedin: [@majidarabi](https://www.linkedin.com/in/majid-arabi-673855129/)
- Telegram: [@one_developer](https://t.me/one_developer)
- Site: http://one-developer.ir
- Email: majidarabi73@gmail.com
