# DiaryWithMusic
## 项目简述
### 1.功能描述：
在对SQLite的基础应用基础上做的日记，可以添加内容和删除内容。用Thread启动一个子线程，在子线程中开启服务，开始播放歌曲，同时可以拖动seekbar改变播放的位置。

### 2.实现细节：

- 第一个FloatingActionButton打开一个新的activity实现日记的添加。
- ListView实现了日记列表的显示功能。
- SQLite实现了日记数据的储存功能。
- seekbar配合Service实现开始播放和选择播放进度的功能。

### 3.不足之处：
- 没有能实现从手机中读取已有歌曲，进行选择播放的功能。
- 界面比较简陋，特别难看。

## 项目感想
- 利用Service中重写的onBind方法返回一个继承Binder类的新对象，可以很方便的实现Activity和Service的通讯，事实上Binder还可以实现进程间的通讯。

## 项目链接
- github: https://github.com/tulensayyj/DiaryWithMusic
