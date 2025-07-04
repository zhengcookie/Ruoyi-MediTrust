// 类型可以参照：https://www.iana.org/assignments/media-types/media-types.xhtml
// 部分后缀对应类型可参照：https://tool.oschina.net/commons
// 以下为部分类型，没有的需要自己处理

const typeMap = new Map();
typeMap.set("xls", "application/vnd.ms-excel"); // Microsoft Excel
typeMap.set("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Microsoft Excel (OpenXML)
typeMap.set("csv", "text/csv"); // CSV
typeMap.set("doc", "application/msword"); // Microsoft Word
typeMap.set("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // Microsoft Word (OpenXML)
typeMap.set("pdf", "application/pdf"); // PDF
typeMap.set("ppt", "application/vnd.ms-powerpoint"); // Microsoft PowerPoint
typeMap.set("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"); // Microsoft PowerPoint (OpenXML)
typeMap.set("png", "image/png"); // 便携式网络图形（PNG）
typeMap.set("gif", "image/gif"); // GIF
typeMap.set("jpeg", "image/jpeg"); // JPEG 图片
typeMap.set("jpg", "image/jpeg"); // JPEG 图片
typeMap.set("mp3", "audio/mpeg"); // MP3 音频
typeMap.set("aac", "audio/aac"); // AAC 音频
typeMap.set("html", "text/html"); // 超文本标记语言 (HTML)
typeMap.set("css", "text/css"); // CSS
typeMap.set("js", "text/javascript"); // JavaScript
typeMap.set("json", "application/json"); // JSON 格式
typeMap.set("abw", "application/x-abiword"); // AbiWord 文档
typeMap.set("arc", "application/x-freearc"); // 存档文档(多个文件嵌入)
typeMap.set("avi", "video/x-msvideo"); // AVI: 音频视频交错
typeMap.set("azw", "application/vnd.amazon.ebook"); // 亚马逊Kindle电子书格式
typeMap.set("bin", "application/octet-stream"); // 任何类型的二进制数据
typeMap.set("bmp", "image/bmp"); // Windows OS/2位图图形
typeMap.set("bz", "application/x-bzip"); // BZip 存档
typeMap.set("bz2", "application/x-bzip2"); // BZip2 存档
typeMap.set("csh", "application/x-csh"); // C-Shell 脚本
typeMap.set("eot", "application/vnd.ms-fontobject"); // MS嵌入式OpenType字体
typeMap.set("epub", "application/epub+zip"); // 电子出版物(EPUB)
typeMap.set("htm", "text/html"); // 超文本标记语言 (HTML)
typeMap.set("ico", "image/vnd.microsoft.icon"); // Icon 格式
typeMap.set("ics", "text/calendar"); // iCalendar 格式
typeMap.set("jar", "application/java-archive"); // Java Archive (JAR)
typeMap.set("jsonld", "application/ld+json"); // JSON-LD 格式
typeMap.set("mid", "audio/midi audio/x-midi"); // 乐器数字接口(MIDI)
typeMap.set("midi", "audio/midi audio/x-midi"); // 乐器数字接口(MIDI)
typeMap.set("mjs", "text/javascript"); // JavaScript 模块
typeMap.set("mpeg", "video/mpeg"); // MPEG 视频
typeMap.set("mpkg", "application/vnd.apple.installer+xml"); // 苹果安装程序包
typeMap.set("odp", "application/vnd.oasis.opendocument.presentation"); // OpenDocument演示文档
typeMap.set("ods", "application/vnd.oasis.opendocument.spreadsheet"); // OpenDocument 电子表格文件
typeMap.set("odt", "application/vnd.oasis.opendocument.text"); // OpenDocument 文本文档
typeMap.set("oga", "audio/ogg"); // OGG 音频
typeMap.set("ogv", "video/ogg"); // OGG 视频
typeMap.set("ogx", "application/ogg"); // OGG
typeMap.set("otf", "font/otf"); // OpenType 字体
typeMap.set("rar", "application/x-rar-compressed"); // RAR 存档
typeMap.set("rtf", "application/rtf"); // 富文本格式 (RTF)
typeMap.set("sh", "application/x-sh"); // Bourne shell 脚本
typeMap.set("svg", "image/svg+xml"); // 可缩放矢量图形 (SVG)
typeMap.set("swf", "application/x-shockwave-flash"); // 小型web格式 (SWF) or Adobe Flash document
typeMap.set("tar", "application/x-tar"); // Tape 归档(TAR)
typeMap.set("tif", "image/tiff"); // 标记图像文件格式 (TIFF)
typeMap.set("tiff", "image/tiff"); // Tagged Image File Format (TIFF)
typeMap.set("ttf", "font/ttf"); // TrueType 字体
typeMap.set("txt", "text/plain"); // Text
typeMap.set("vsd", "application/vnd.visio"); // Microsoft Visio
typeMap.set("wav", "audio/wav"); // 波形音频格式
typeMap.set("weba", "audio/webm"); // WEBM 音频
typeMap.set("webm", "video/webm"); // WEBM 视频
typeMap.set("webp", "image/webp"); // WEBP 图片
typeMap.set("woff", "font/woff"); // 网页开放字体格式 (WOFF)
typeMap.set("woff2", "font/woff2"); // 网页开放字体格式 (WOFF)
typeMap.set("xhtml", "application/xhtml+xml"); // XHTML
typeMap.set("xml", "text/xml"); // XML application/xml（普通用户不可读）、text/xml（普通用户可读）
typeMap.set("xul", "application/vnd.mozilla.xul+xml"); // XUL
typeMap.set("zip", "application/zip"); // ZIP
typeMap.set("3gp", "video/3gpp"); // 3GPP audio/video 容器 video/3gpp、audio/3gpp（不含视频）
typeMap.set("3g2", "video/3gpp2"); // 3GPP2 audio/video 容器 video/3gpp2、audio/3gpp2（不含视频）
typeMap.set("7z", "application/x-7z-compressed"); // 7-zip

export default typeMap;

const getContentType = function (filename: string): string {
  if (!filename || filename.trim() == "") {
    throw "filename is null";
  }
  const name = filename.split(".");
  if (name.length < 2) {
    return "text/xml";
  }

  if (typeMap.get(name[name.length - 1])) {
    return typeMap.get(name[name.length - 1]);
  }

  return "text/xml";
};

export { getContentType };
