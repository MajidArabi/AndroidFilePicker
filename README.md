# FilePicker

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.MajidArabi:FilePicker:0.0.1'
	}
	

# Usage

  FilePicker.show(
                activity = this,
                gridSpanCount = 3,
                limitItemSelection = 3,
                fileType =  FileType.VIDEO or FileType.IMAGE,
                listDirection = ListDirection.RTL or ListDirection.LTR,
            ) { selectedFiles ->
                Log.e("TAG", "$selectedFiles")
            }
