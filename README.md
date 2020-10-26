# jixiv
用來實現pixiv爬蟲等的Java框架

![jixiv](https://github.com/Huanying04/jixiv/blob/master/image/jixiv.png)
# 功能
* 獲取插畫/漫畫信息
* 獲取小說信息
* 下載插畫
* 下載動圖(zip 內含動圖所有影格)
* 下載漫畫
* 下載畫師所有作品
* 下載小說封面
* 獲取小說內容
* 排行榜
* 搜尋
* etc.
# 配置方法
(尚無，將會在完善後公開至Maven Central)

(可暫時用jar代替)
# 使用方法
## PHPSESSID
由於pixiv防爬蟲防得很嚴或是我的程式能力很弱，目前做不出帳號密碼模擬登入。可暫時藉由PHPSESSID來模擬登入。

![phpsession](https://github.com/Huanying04/Huanying04/blob/main/phpsession.png)

在瀏覽器中找到pixiv cookie中的PHPSESSID即可用之來模擬登入。
## 獲取插畫/漫畫信息
```java
String phpSession = ""; //pixiv登入後cookie裡的PHPSESSID
int id = 85209753; //插畫id
PixivIllustration pi = new PixivIllustration(phpSession);
//獲取插畫標題
String title = pi.getTitle(id);
//獲取插畫簡介
String description = pi.getRawDescription(id);
//獲取插畫所有標籤名字
String[] tags = pi.getTags(id);
//獲取插畫頁數
int pageCount = pi.getPageCount(id);
//獲取觀看次數
int viewCount = pi.getViewCount(id);
//獲取收藏次數
int bookmarkCount = pi.getBookmarkCount(id);
//獲取讚數
int likeCount = pi.getLikeCount(id);
//獲取評論數
int commemtCount = pi.getCommentCount(id);
```
## 獲取小說信息
```java
int id = 11387000; //小說id
PixivNovel pn = new PixivNovel(phpSession);
//獲取小說標題
String title = pn.getTitle(id);
//獲取小說簡介
String description = pn.getRawDescription(id);
//獲取小說所有標籤名字
String[] tags = pn.getTags(id);
//獲取頁數
int pageCount = pn.getPageCount(id);
//獲取總字數
int textCount = pn.getTextCount(id);
//獲取觀看次數
int viewCount = pn.getViewCount(id);
//獲取收藏次數
int bookmarkCount = pn.getBookmarkCount(id);
//獲取讚數
int likeCount = pn.getLikeCount(id);
//獲取評論數
int commemtCount = pn.getCommentCount(id);
```
## 下載插畫/漫畫
### 指定頁數
#### 完整版
可以指定第幾頁及插畫大小。
```java
String path = "";  //儲存位置
int id = 85209753;  //插畫id
int page = 0;  //頁碼
PixivImageSize size = PixivImageSize.Original;  //圖片大小
PixivIllustration pi = new PixivIllustration(phpSession);
pi.download(path, id, page, size);  //將id為85209753的插畫的第0頁下載到path
```
#### 簡易版
只需id就足以完成
```java
String path = "";  //儲存位置
int id = 85209753;  //插畫id
PixivIllustration pi = new PixivIllustration(phpSession);
pi.download(path, id);  //將id為85209753的插畫的第0頁下載到path
```
### 所有頁數
```java
String path = "";  //資料夾位置
int id = 85207001;  //插畫id
PixivIllustration pi = new PixivIllustration(phpSession);
pi.downloadAll(path, id, PixivImageSize.Original);  //將id中的所有插畫都下載到path裡
```
### 使用者所有插畫
```java
String path = "";  //資料夾位置
int id = 5445450;  //使用者id
PixivImageSize size = PixivImageSize.Original;  //圖片大小
PixivIllustration pi = new PixivIllustration(phpSession);
pi.downloadUserAll(path, id, size);  //將使用者id中的所有插畫及漫畫都下載到path裡
```
## 下載動圖(zip)
```java
String path = "";  //資料夾位置
int id = 44298467;  //插畫id
PixivIllustration pi = new PixivIllustration(phpSession);
pi.downloadUgoiraZip(path, id);
```
## 獲取小說內容
```java
int id = 11387000; //小說id
PixivNovel pn = new PixivNovel(phpSession);
String content = pn.getContent(id);  //小說內容
```
## 下載小說封面
```java
String path = "";  //儲存位置
int id = 11387000; //小說id
PixivNovel pn = new PixivNovel(phpSession);
pn.downloadCover(path, id);
```
## 作品是否為R-18或R-18G
如果要判斷作品為成人限制作品，可使用下面方法判斷。
```java
PixivArtwork.isNSFW(id);
```
範例：
```java
PixivIllustration pi = new PixivIllustration(phpSession);  //插畫或漫畫
PixivNovel pn = new PixivNovel(phpSession);  //小說
pi.isNSFW(id);  //作品(插畫或漫畫)id是否為成人限制
pn.isNSFW(id);  //作品(小說)id是否為成人限制
```
如果只要判斷R-18或R-18G則為
```java
PixivArtwork.isR18(id);  //作品id是否為R-18
PixivArtwork.isR18G(id);  //作品id是否為R-18-G
```
## 排行榜
可使用下面的方式獲取今日綜合排行榜
```java
Pixiv p = new Pixiv(phpSession);
int page = 1;  //頁碼
PixivRank pr = p.rank(page);
```
或是使用下列方法獲取指定排行榜
```java
Pixiv p = new Pixiv(phpSession);
int page = 1;  //頁碼
PixivRankMode mode = PixivRankMode.Daily;  //排行榜類別
PixivRankContent content = PixivRankContent.Illust;  //作品形式
String date = "20201001";  //日期
PixivRank pr = p.rank(page, mode, content, date);
```
## 搜尋
(待補充)
