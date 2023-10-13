layui.use('form', function(){  //如果只加载一个模块，可以不填数组。如：layui.use('form')
  var form = layui.form //获取form模块
  //监听提交按钮
  form.on('submit(test)', function(data){
    console.log(data);
  });

});
