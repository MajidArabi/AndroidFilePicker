

[![](https://jitpack.io/v/MajidArabi/FilePicker.svg)](https://jitpack.io/#MajidArabi/FilePicker)

## Simple, Powerful and Beautiful Android Image or Video Picker 😎
**Features** 😍
 - Check storage permission
 - Single and multiple selection
 - Supported RTL and LTR list direction (default=LTR)
 - Supported image and video (default=image)
 - Supported custom title
 - Supported dynamic span count (default=2)
 - Limit max item selection (default=1)
 - Show file directory
 - Show file size

## Screenshots

| Sample | Pick Image | Pick Video
|--|--|--|
| <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/sample.png" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/pick-image.png" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/screenshots/pick-video.png" width="250" />

## Download

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.majidarabi:AndroidFilePicker:0.0.2'
	}

## Usage
	
	FilePicker.show(
	    activity = this,
	    gridSpanCount = 3,
	    cancellable = false,
	    limitItemSelection = 3,
	    title = "Select Media",
	    submitText = "Submit Files",
	    listDirection = ListDirection.RTL,
	    fileType = if (video) FileType.VIDEO else FileType.IMAGE,
	) {
		// Do something here with selected files
	}

#### Kotlin

	showFilePicker { 
		// Do something here with selected files
	}

## Author

**Majid Arabi**
- Github : [@majidarabi](https://github.com/MajidArabi)
- Linkedin: [@majidarabi](https://www.linkedin.com/in/majid-arabi-673855129/)
- Telegram: [@one_developer](https://t.me/one_developer)
- Site : http://one-developer.ir
- Email : majidarabi73@gmail.com
