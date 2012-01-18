说明
========
此代码用于实现一个android上的集单词查询和单词背诵为一体的应用，我在2011年元旦时初步了解了一下android的开发，但实现的相当弱，代码也很烂；我现在手机上有一个叫译典通的工具，用起来不错，但少了我需要的熟悉度功能，因此我还是倾向于自己做一个，后续我会逐渐优化整理这份代码，使其能够满足自己的日常使用。

此代码使用emacs+ant开发（eclipse在我的机器上表现实在不理想），字典查询部分使用的dictcn的xml api，为了音标显示，集成了一个segoeui的字体，面向android 2.3.x版本。

更新记录
========

* 20111226
Merry Christmas! 修复本地查找的问题，目前本地查询功能已经正确，后续将致力于优化界面和解决BUG。

* 20111217
重新整理代码工程，并上传至github管理。

* 20120118
1.0-pre1版本发布，支持原计划的各种功能（本地词典也已支持），但UI上需要进一步优化。

遗留问题
=========
* seagoui.ttf这个字体是私有字体(官方网址)[http://www.ascendercorp.com/font/segoe-ui/] ，我现在上传在这里，应当会有版权上的问题，不过像我们这样的屁民，应当没太大的风险，后续我会换成开源字体。
* stardict的词典似乎都有版权上的问题（甚至因为这个原因，stardict也从sf上转移到了google code上），关于词典的问题，暂时先不考虑，除非github会管。

下载方式
========
* 安装包（未签名）：bin/recitedictcn-debug-{version}.apk
* 字典（朗道中英字典，使用与stardict相同格式的词典库，但额外生成了一个索引文件）下载：bin/langdao_ec_gb.tar.gz
  字典下载后，请将字典包中的５个文件放在sdcard/recitedictcn的目录（目录请自行创建）中。