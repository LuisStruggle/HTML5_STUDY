// 个人认为worker的工作原理，在后台一直运行某个文件

var i = 0;

onmessage = function(event){
	console.log(event.data);
}

function timedCount() {
    i = i + 1;
    // 以上代码中重要的部分是 postMessage() 方法 - 它用于向 HTML 页面传回一段消息。
    postMessage(i);

    // 过timer秒钟后去执行func函数
    setTimeout("timedCount()", 500);
}

timedCount();