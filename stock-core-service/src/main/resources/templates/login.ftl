<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>stock operation login</title>
    <link rel="stylesheet" href="css/layui.css">
</head>
<body>

<form action="/login" method="post">
<div class="layui-container">
    <div class="layui-progress" style="margin: 15px 0 30px;">
        <div class="layui-progress-bar" lay-percent="100%"></div>
    </div>
    <div class="layui-container">
        <div class="layui-row" >
            ${errorMsg?default('')}
        </div>
    </div>
    <ul class="layui-form layui-form-pane" style="margin: 15px;">
    <li class="layui-form-item">
        <label class="layui-form-label">手机号:</label>
        <div class="layui-input-block">
            <input class="layui-input" lay-verify="required|phone" name="mobilePhone"></div>
    </li>
        <li class="layui-form-item">
            <label class="layui-form-label">密码:</label>
            <div class="layui-input-block">
                <input class="layui-input" lay-verify="required" name="password"></div>
        </li>
        <li class="layui-form-item">
            <label class="layui-form-label">验证码:</label>
            <div class="layui-input-block">
                <input class="layui-input" lay-verify="required" name="verifyCode"></div>
        </li>
        <li class="layui-form-item" style="text-align:center;">
            <button type="submit" lay-submit lay-filter="login" class="layui-btn">登录</button>
        </li>
    </ul>

</div>
</form>
<!-- body 末尾处引入 layui -->
<script src="layui.js"></script>
<script>
layui.use('form',function(){
  var form = layui.form
  ,util = layui.util;
  //触发事件
  //监听提交按钮
  form.on('submit(login)', function(data){
        //提交到表单
  });
});
</script>
</body>
</html>