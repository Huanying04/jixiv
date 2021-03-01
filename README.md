[![Maven Central](https://img.shields.io/maven-central/v/com.github.huanying04.utils/jixiv.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.huanying04.utils%22%20AND%20a:%22jixiv%22)
# jixiv
用來實現pixiv爬蟲等的Java函式庫

<p align="center"><img src="https://github.com/Huanying04/jixiv/blob/master/image/jixiv.png" width="50%"></p>

# 功能
* 獲取插畫/漫畫信息
* 獲取小說信息
* 喜歡作品
* 添加/取消收藏作品
* 下載插畫
* 下載動圖(zip 內含動圖所有影格)
* 下載漫畫
* 下載畫師所有作品
* 下載小說封面
* 獲取小說內容
* 排行榜
* 搜尋
* 獲取已關注用戶的最新作品
* 獲取用戶收藏
* 設定作品瀏覽限制
* etc.
# 配置方法
## Maven
```xml
<dependencies>
  <dependency>
    <groupId>com.github.huanying04.utils</groupId>
    <artifactId>jixiv</artifactId>
    <version>VERSION</version>
  </dependency>
</dependencies>
```
## Gradle
```gradle
dependencies {
    implementation 'com.github.huanying04.utils:jixiv:VERSION'
}
```
# 使用方法
Doc：https://huanying04.github.io/Jixiv-Wiki/
## PHPSESSID
由於pixiv防爬蟲防得很嚴或是我的程式能力很弱，目前做不出帳號密碼模擬登入。可暫時藉由PHPSESSID來模擬登入。

![phpsession](https://github.com/Huanying04/Huanying04/blob/main/phpsession.png)

在瀏覽器中找到pixiv cookie中的PHPSESSID即可用之來模擬登入。
## 模擬登入(必要！)
```java
String phpSession = "00000000_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; //pixiv登入後cookie裡的PHPSESSID
String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36";  //你的User-Agent

Jixiv.loginByCookie(phpSession);
Jixiv.setUserAgent(userAgent);
```

## 獲取插畫/漫畫信息
```java
int id = 85209753; //插畫id
IllustrationInfo iInfo = Illustration.getInfo(id);  //注意這個Illustration是net.nekomura.utils.jixiv.artworks裡的
//獲取插畫標題
String title = iInfo.getTitle();
//獲取插畫簡介
String description = iInfo.getRawDescription();
//獲取插畫所有標籤名字
String[] tags = iInfo.getTags();
//獲取插畫頁數
int pageCount = iInfo.getPageCount();
//獲取觀看次數
int viewCount = iInfo.getViewCount();
//獲取收藏次數
int bookmarkCount = iInfo.getBookmarkCount();
//獲取讚數
int likeCount = iInfo.getLikeCount();
//獲取評論數
int commemtCount = iInfo.getCommentCount();
```
## 獲取小說信息
```java
int id = 11387000; //小說id
NovelInfo nInfo = Novel.getInfo(id);  //注意這個Novel是net.nekomura.utils.jixiv.artworks裡的
//獲取小說標題
String title = nInfo.getTitle();
//獲取小說簡介
String description = nInfo.getRawDescription();
//獲取小說所有標籤名字
String[] tags = nInfo.getTags();
//獲取頁數
int pageCount = nInfo.getPageCount();
//獲取總字數
int textCount = nInfo.getTextCount();
//獲取觀看次數
int viewCount = nInfo.getViewCount();
//獲取收藏次數
int bookmarkCount = nInfo.getBookmarkCount();
//獲取讚數
int likeCount = nInfo.getLikeCount();
//獲取評論數
int commemtCount = nInfo.getCommentCount();
```
## 喜歡作品
```java
int illustId = 66715075;
Illustration.like(illustId);  //喜歡id為66715075的插畫

int novelId = 14488459;
Novel.like(novelId);  //喜歡id為14488459的小說
```
## 收藏作品
```java
int illustId = 87143893;
Illustration.addBookmark(illustId);  //收藏id為87143893的插畫
        
int novelId = 14488459;
Novel.addBookmark(novelId);  //收藏id為14488459的小說
```
## 取消收藏作品
```java
int illustId = 87143893;
Illustration.removeBookmark(illustId);  //取消收藏id為87143893的插畫
        
int novelId = 14488459;
Novel.removeBookmark(novelId);  //取消收藏id為14488459的小說
```
## 評論
```java
Illustration.comment(87135236, "かわいい");  //在id為87135236的插畫底下評論"かわいい"
Illustration.comment(87148546, "しゅき" + Emoji.HEART);  //也可以使用pixiv的emoji留言
Illustration.comment(87013815, Emoji.HEART.toString());  //如果只有評論單個emoji的話要使用toString()方法
Illustration.comment(84522989, Stamp.STAMP_205); //或者也可以使用表情貼圖評論

//小說同理，只不過要使用Novel類的靜態方法
//Novel.comment(......);
```
## 獲取評論
```java
Comments cs1 = Illustration.getComments(85786994, 0, 3);  //獲取id為85786994的插畫最新的3個評論
Comments cs2 = Illustration.getComments(85786994, 3, 50);  //獲取id為85786994的插畫除了最新的3個，最新的50個評論(可能不達50，如果評論數沒那麼多的話)
Comments cs = Illustration.getComments(85786994);  //或者不指定個數直接獲取所有評論
Comment c = cs.getComment(0);  //獲取最新一個評論的評論物件
c.getComment();  //獲取評論內容

//小說同理
//Comments cs3 = Novel.getComments(......);
```
## 下載插畫/漫畫
### 指定頁數
#### 完整版
可以指定第幾頁及插畫大小。
```java
String path = "";  //儲存位置
int id = 86067804;  //插畫id
int page = 0;  //頁碼
PixivImageSize size = PixivImageSize.Original;  //圖片大小
IllustrationInfo iInfo = Illustration.get(id);
iInfo.download(path, page, size);  //將id為85209753的插畫的第0頁下載到path
```
#### 簡易版
只需id就足以完成
```java
String path = "";  //儲存位置
int id = 87148677;  //插畫id
IllustrationInfo iInfo = Illustration.get(id);
iInfo.download(path);  //將id為85209753的插畫的第0頁下載到path
```
### 所有頁數
```java
String path = "";  //資料夾位置
int id = 85207001;  //插畫id
IllustrationInfo iInfo = Illustration.get(id);
iInfo.downloadAll(path, PixivImageSize.Original);  //將id中的所有插畫都下載到path裡
```
## 下載使用者所有插畫
```java
String path = "";  //資料夾位置
int userId = 5445450;  //使用者id
PixivImageSize size = PixivImageSize.Original;  //圖片大小
Pixiv.downloadUserAllIllustration(path, userId, size);  //將使用者id中的所有插畫及漫畫都下載到path裡
```
## 下載動圖(zip)
```java
String path = "";  //資料夾位置
int id = 44298467;  //插畫id
IllustrationInfo iInfo = Illustration.getInfo(id);
iInfo.downloadUgoiraZip(path);
```
## 獲取小說內容
```java
int id = 11387000; //小說id
NovelInfo nInfo = Novel.getInfo(id);
String content = nInfo.getContent();  //小說內容
```
## 下載小說封面
```java
String path = "";  //儲存位置
int id = 11387000; //小說id
NovelInfo nInfo = Novel.getInfo(id);
n.downloadCover(path);
```
## 作品是否為R-18或R-18G
如果要判斷作品為成人限制作品，可使用下面方法判斷。
```java
ArtworkInfo.isNSFW();
```
範例：
```java
IllustrationInfo iInfo = Illustration.get(id);
PixivNovelInfo nInfo = Novel.get(id);

iInfo.isNSFW(id);  //作品(插畫或漫畫)id是否為成人限制
nInfo.isNSFW(id);  //作品(小說)id是否為成人限制
```
如果只要判斷R-18或R-18G則為
```java
ArtworkInfo.isR18();  //作品id是否為R-18
ArtworkInfo.isR18G();  //作品id是否為R-18-G
```
## 排行榜
可使用下面的方式獲取今日綜合排行榜
```java
int page = 1;  //頁碼
Rank rank = Pixiv.rank(page);
```
或是使用下列方法獲取指定排行榜
```java
int page = 1;  //頁碼
PixivRankMode mode = PixivRankMode.Daily;  //排行榜類別
PixivRankContent content = PixivRankContent.Illust;  //作品形式
String date = "20201001";  //日期
Rank rank = Pixiv.rank(page, mode, content, date);
```
## 搜尋
可使用下面方法關鍵字搜尋插畫。
```java
String keyword = "";  //關鍵字
int page = 1;  //頁碼
SearchResult result = Pixiv.search(keyword, page);
```
或是使用下面方法搜尋。
```java
String keyword = "";  //關鍵字
int page = 1;  //頁碼
PixivSearchArtistType artistType = PixivSearchArtistType.Illustrations;  //搜尋作品類別
PixivSearchOrder order = PixivSearchOrder.NEW_TO_OLD;  //排序方式
PixivSearchMode mode = PixivSearchMode.ALL;  //搜尋作品年齡分類
PixivSearchSMode sMode = PixivSearchSMode.S_TAG;  //關鍵字搜尋方式
PixivSearchType type = PixivSearchType.Illust;  //搜尋作品類別
SearchResult result = Pixiv.search(keyword, page, artistType, order, mode, sMode, type);
```


# 示例
* [Pixiv Client](https://github.com/Huanying04/Pixiv-Client)
* 依賴於**jixiv**及[Java Discord API](https://github.com/DV8FromTheWorld/JDA)開發之Discord機器人[Nekobot](https://github.com/Huanying04/Nekobot)
