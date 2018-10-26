# XPermission
## 介绍
XPermission是一个适配Android6.0运行时权限的库，支持API11，暂时只解决6.0及以上的Android手机适配问题，包括国产手机，有些国产厂商再5.0就开始自己搞运行时权限，这个库暂时不考虑各种6.0以下的适配问题，目前是按照android官方的标准来搞运行时权限的适配。

## 引用方法
*Gradle
```
compile 'com.paozhuanyinyu:permission:1.0.0'
compile 'io.reactivex.rxjava2:rxjava:2.1.0'//由于依赖RxJava2,所以使用时需引入
```
*Maven
```
<dependency>
  <groupId>com.paozhuanyinyu</groupId>
  <artifactId>permission</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
<dependency>
  <groupId>io.reactivex.rxjava2</groupId>
  <artifactId>rxjava</artifactId>
  <version>2.0.6</version>
  <type>pom</type>
</dependency>
```

## 使用
```
XPermission.getInstance()
            .requestEach(context,new Params(Manifest.permission.Camera, "拍照"))
            //这里只要是Context就行，不限于Activity,Fragment,且其他代码处也可以使用
            .subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if(permission.granted){
                        //已授权回调
                        Toast.makeText(MainActivity.this,"授权成功",Toast.LENGTH_SHORT).show();
                    }else{
                        //拒绝授权回调
                        Toast.makeText(MainActivity.this,"拒绝授权",Toast.LENGTH_SHORT).show();
                    }
                }
            });
```

## Thanks 
[RxPermission](https://github.com/tbruyelle/RxPermissions)

[permission4m](https://github.com/jokermonn/permissions4m)
## License

Copyright  2017  paozhuanyinyu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.