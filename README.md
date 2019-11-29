# Waziup Deployment Assistant

![Travis-ci](https://api.travis-ci.org/Waziup/Waziup-deployment-assistant.svg)

This repository contains the deployment assistant android application that is to guide the deployment team when installing devices. It also collects meta-data about the devices and gateway deployed.

**Waziup Documentations:**
  <br>
- [USERMANUAL](http://www.waziup.io/documentation)
  <br>
- [OUR SOLUTIONS](http://www.waziup.io/solutions/deployassist)
  <br>

## Architecture Blueprint
![Blueprint](https://janishar.github.io/images/mvp-app-pics/mvp-arch.png)
<br>

## Project Structure
![Structure](https://janishar.github.io/images/mvp-app-pics/mvp-project-structure-diagram.png)
<br>

#### The app has following packages:
1. **data**: It contains all the data accessing and manipulating components.
2. **di**: Dependency providing classes using Dagger2.
3. **ui**: View classes along with their corresponding Presenters.
4. **service**: Services for the application.
5. **utils**: Utility classes.

#### Classes have been designed in such a way that it could be inherited and maximize the code reuse.

### Library reference resources:
1. RxJava2: https://github.com/amitshekhariitbhu/RxJava2-Android-Samples
2. Dagger2: https://github.com/MindorksOpenSource/android-dagger2-example
3. Retrofit : https://github.com/square/retrofit
4. AndroidDebugDatabase: https://github.com/amitshekhariitbhu/Android-Debug-Database
5. ButterKnife: http://jakewharton.github.io/butterknife/

## Install
### Install from command line
- **On Windows**:
  ```
  gradlew task-name
  ```
- **On Mac or Linux**:
  ```
  ./gradlew task-name
  ```
for more information on how to build and run from command line check [google developer documentation](https://developer.android.com/studio/build/building-cmdline)

## Install with Docker

Compile docker image:
```
docker build -t waziup/gradle .
```
You may need to delete the content of ./app/build before.
Then compile the APK:
```
docker run --rm -v "$PWD":/home/gradle/MyApp waziup/gradle
```
Your APK can be found in ./app/build/outputs/apk

