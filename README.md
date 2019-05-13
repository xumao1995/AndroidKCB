# AndroidKCB
安卓课程表app
2	系统设计
4.1概要分析
从程序流程来分，用户在第一次进入程序时，需要通过手动添加课程或者通过学号和密码登录URP教务系统进行自动导入课程。手动添加课程时，可以滑动菜单栏进行单双周设置，用户在课程表界面可以通过单击课程来显示课程详细信息，通过长按课程来删除课程。

4.2数据库分析
		本程序数据存取方面主要用到了SQLite数据库来存储课程信息。

4.2.1	SQLite数据表设计
		SQLite数据库是Android系统中非常重要的数据存储方法，它是Android系统唯一支持的数据库类型，也是Android应用程序进行持久化存储的三种方式之一。SQLite 数据库是SQL数据库的简化版，支持大部分SQL操作。SQLite 是Android应用程序中非常常用的一种数据存储手段。
（1）	数据库表的设计
	根据对本程序的需求分析，本程序仅需要一张course表用来存储已经添加的课程，用户每添加一个课程就会在该表中新增一条记录，并且自动生成一个唯一的id,用以区别各个课程，同样名称但是分成两次添加的课程会被认为是不同的课程，这样设计的目的主要是为了实现同一个课程可能在一周会开设多次。
	该表结构设计如下:
字段名	数据类型	是否主键	说明
id	integer	是	课程编号
course_name	text	否	课程名称
teacher	text	否	任课教师
class_room	text	否	上课地点
day	integer	否	上课星期
class_start	integer	否	课程开始节次
class_end	integer	否	课程结束节次
week	text	否	单双周
	该数据库表的设计目的是为了保存整个星期的课程，这样实现整个星期的课程排布，包括数据的存储，课程查看都比较方便。
（2）	SQLite数据库的创建
		创建数据库主要用到了DataBaseHelper类，该类继承了SQLiteOpenHelpe类，实现了数据库、数据表的创建。具体方法如下：
	public class DatabaseHelper extends SQLiteOpenHelper{
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table courses(" +
                "id integer primary key autoincrement," +
                "course_name text," +
                "teacher text," +
                "class_room text," +
                "day integer," +
                "class_start integer," +
                "class_end integer,"+
                "week text)");		
				}
}

（3）	保存课程信息到数据库
	private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher, class_room, day, class_start, class_end) " + "values(?, ?, ?, ?, ?, ?)",
                        new String[] {course.getCourseName(),
                                course.getTeacher(),
                                course.getClassRoom(),
                                course.getDay()+"",
                                course.getStart()+"",
                                course.getEnd()+"",
                                course.getWeek()
                        }
    }

（4）	从数据库中读取课程信息
	private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end")),
                        cursor.getString(cursor.getColumnIndex("week"))));
            } while(cursor.moveToNext());
        }
        cursor.close();
}

4.3程序中的主要类

4.3.1 MainActivity类
该类为整个程序的主Activity，显示课程表的主界面。其主要方法如下：
onCreate：Activity主要重写方法之一，实现程序初始化和UI展示。包括从数据库中加载课程信息到课程视图。
onNavigationItemSelected：实现菜单栏点击事件响应。
createCourseView：创建课程视图。
loadData：从数据库中读取课程信息。
saveData：向数据库中写入课程信息。
initDate：获取系统时间，并进行格式化处理。
initImage：获取系统头像，并进行格式化处理。
change_image：用户修改自定义头像，可从本地获取，也可以调用系统相机进行拍照。
showTypeDialog：显示头像修改界面，提供拍照和从本地选取两种方法。
cropPhoto：对选取的头象进行裁剪和缩放。

4.3.2 AddCourseActivity类
该类为实现添加课程界面的Activity，主要实现由用户选择和输入课程信息，并将课程信息添加到数据库中。
onCreate：初始化UI控件，将前端text控件与后端数据进行绑定，便于后端能够有效的获取前端输入的课程信息。
onClick：响应完成按钮点击事件。
setResult：向MainActivity活动传入刚录入的课程信息，便于将该课程迅速加入课程视图中进行显示。

4.3.3 Course类
该类为课程实体类，主要包括课程属性和对应的get、set方法。

4.3.4 Login类
该类为实现登录界面的Activity类，主要实现从URP网站下载验证码到本地并将该验证码加载到视图中进行显示，同时提供用户输入学号和密码。
onCreate：初始化UI，并将前端控件与后端进行绑定。
getLocalBitmap：从本地获取图片，并加载到UI视图。
DownLoadThread：为一个子线程类，因为Android不允许子线程更新UI线程，所以必须创建一个子线程来请求URP网站首页（登录界面）来获取网站登录必备参数之一——验证码及对应cookies。
PositThread：同样为一个子线程，利用DownLoadThread下载下来的验证码和cookies，再加上用户输入的学号和密码，封装为登录URP系统必须的参数，执行登录请求，登录成功后，返回一个有权限访问后续网站（课程表、成绩）的cookies，便于后续爬取课程表及成绩。

4.3.5 MessageCourseActivity类
该类为显示课程详细信息界面的activity类，主要用于显示课程详细信息，包括课程名，任课教师，上课地点及课程节次。

4.3.6 CircleImgageView类
该类主要用于实现圆形头像框，将头像裁剪为圆形，显得更加优美。

4.3.7 DataBaseHelp类
该类主要用于创建数据表，便于实现课程信息的持久化存储。
