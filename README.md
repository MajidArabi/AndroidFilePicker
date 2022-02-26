
[![](https://jitpack.io/v/MajidArabi/FilePicker.svg)](https://jitpack.io/#MajidArabi/FilePicker)

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
	        implementation 'com.github.MajidArabi:FilePicker:0.0.2'
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

simple usage in activity and fragment with kotlin extension function:

## Example

	showFilePicker { 
		// Do something here with selected files
	}


## Screenshots

| Sample | Pick Image | Pick Video
|--|--|--|
| <img src="https://github.com/MajidArabi/FilePicker/blob/master/Screenshot_20220225-232932.png" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/Screenshot_20220225-233059.png" width="250" /> | <img src="https://github.com/MajidArabi/FilePicker/blob/master/Screenshot_20220225-233013.png" width="250" />
