# JXPrintDemo
打印demo

**依赖说明**：

1.在项目的build.gradle中添加以下
```xml
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在modle的依赖中添加以下
```xml
	dependencies {
		implementation 'com.github.SpeedataG:Device:1.6.8'
		implementation 'com.github.SpeedataG:Print:1.0.1'
	}

 ```