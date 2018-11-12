# Waziup Deployment Assistance

This repository contains the deployment assistant android application that is to guide the deployment team when installing sensors. It also collects meta-data about the sensors and gateway deployed.

Documentation: http://www.waziup.io/documentation/api/
<br>

# Architecture Blueprint
![Blueprint](https://janishar.github.io/images/mvp-app-pics/mvp-arch.png)
<br>

# Project Structure
![Structure](https://janishar.github.io/images/mvp-app-pics/mvp-project-structure-diagram.png)
<br>

1. https://blog.mindorks.com/android-mvp-architecture-extension-with-interactors-and-repositories-bd4b51972339)

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

