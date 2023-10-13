<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>测试 - layui</title>
    <link rel="stylesheet" href="../css/layui.css">
    <!-- body 末尾处引入 layui -->
    <script src="../layui.js"></script>
</head>
<body>

<div class="layui-container">
    <div class="layui-progress" style="margin: 15px 0 30px;">
        <div class="layui-progress-bar" lay-percent="100%"></div>
    </div>
    <blockquote class="layui-elem-quote" style="margin-top: 30px;">
        <div class="layui-text">
            <ul>
                <li>总净值：<span><span id="totalAmount">${hkTotally}</span></span></li>
                <li>港净值：<span><span id="hkAmount">${Kfund.assetBalance}</span></span></li>
                <li>美净值：<span><span id="usAmount">${Pfund.assetBalance}</span></span></li>
            </ul>
        </div>
    </blockquote>
    <table id="K" lay-filter="ktable"></table>
    <table id="P" lay-filter="ptable"></table>
</div>

<script>
layui.use('table', function(){
  var table = layui.table;
  //第一个实例
  table.render({
    elem: '#K'
  ,skin: 'line' //行边框风格
  ,even: true //开启隔行背景
  ,size: 'sm' //小尺寸的表格
    ,url: 'queryHolding?exchangeType=K' //数据接口
    ,page: false //开启分页
    ,cols: [[ //表头
       {field: 'stockCode', title: '名称'}
      ,{field: 'keepCostPrice', title: '保本价'}
      ,{field: 'currentCount', title: '数量'}
      ,{field: 'marketValue', title: '仓值'}
      ,{field: 'gapValue', title: '盈亏'}
      ]]
  });

  table.render({
    elem: '#P'
  ,skin: 'line' //行边框风格
  ,even: true //开启隔行背景
  ,size: 'sm' //小尺寸的表格
    ,url: 'queryHolding?exchangeType=P' //数据接口
    ,page: false //开启分页
    ,cols: [[ //表头
       {field: 'stockCode', title: '名称'}
      ,{field: 'keepCostPrice', title: '保本价'}
      ,{field: 'currentCount', title: '数量'}
      ,{field: 'marketValue', title: '仓值'}
      ,{field: 'gapValue', title: '盈亏'}
    ]]
  });
});
</script>
</body>
</html>