/*登录验证跳转界面 */
$(document).ready(function () {
    // 监听键盘事件
    $(document).keypress(function (e) {
        // 判断是否按下Enter键
        if (e.which == 13) {
            // 触发登录按钮的点击事件
            $("#btLogin").click();
        }
    });
    // 监听登录按钮的点击事件
    $("#btLogin").click(function (e) { 
        $.ajax({
            type: "post",
            url: "User/Login.do", // 服务器端地址（相对路径）
            data: {// key value
                userName: $("#userName").val(),
                password: $("#password").val()
            },
            dataType: "json",//接收到的数据封装格式
            // 服务器端程序
            success: function (response) {//接收服务器端response响应
                console.log(response);
                if (response.code == 1) {// 成功，跳转页面
                    sessionStorage.setItem("userName", $("#userName").val());
                    window.location.href = "menu.html";
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    // $("#info").val(response.info); // input
                    $("#info").text(response.info); // span
                }
            }
        });
    });
});

/*获取当前收银员 */
$(document).ready(function () {
    var userName = sessionStorage.getItem("userName"); // 从sessionStorage中检索#userName的值
    $("#operator").text(userName); // span
});


/*收银*/
//将商品条形码和数量和收银员发送到后端
$(document).ready(function () {
    var userName = sessionStorage.getItem("userName");
    // 监听键盘事件
    $(document).keypress(function (e) {
        // 判断是否按下Enter键
        if (e.which == 13) {
            // 触发收银按钮的点击事件
            $("#cash").click();
        }
    });
    // 监听收银按钮的点击事件
    $("#cash").click(function (e) { 
        $.ajax({
            type: "post",
            url: "Saledeta/Cash.do", // 服务器端地址（相对路径）
            data: {// key value
                barCode: $("#barCode").val(),
                count: $("#count").val(),
                userName: userName
            },
            dataType: "json",//接收到的数据封装格式
            // 服务器端程序
            success: function (response) {//接收服务器端response响应
                if (response.code == 1) {// 
                    $("#infoCash").text(response.info);
                    $("#barCode").val("");
                    $("#count").val("");
                    setTimeout(function() {
                        $("#infoCash").text("");
                    }, 2000);
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    // $("#info").val(response.info); // input
                    // $("#info").text(response.info); // span
                    alert(response.info);
                }
                else if (response.code == 3) {// 失败，显示提示信息
                    // $("#info").val(response.info); // input
                    // $("#info").text(response.info); // span
                    alert(response.info);
                }
            }
        });
    });
});
  

//将销售日期发送到后端
$(document).ready(function () {
    $("#statistics").click(function (e) { 
        // 保存表头
        var tableHeader = $('#table_list tr:first');
        // 清空表格中的数据
        $('#table_list').empty();
        // 将表头重新添加到表格中
        $('#table_list').append(tableHeader);
        dt = $("#date").val();
        if(dt == ""){
            alert("请输入日期！");
        }
        else{
            $.ajax({
            type: "post",
            url: "Saledeta/statistics.do", // 服务器端地址（相对路径）
            data: {// key value
                date: $("#date").val()
            },
            dataType: "json",//接收到的数据封装格式
            // 服务器端程序
            success: function(response) {
                var table = $('#table_list');
                for (var i = 0; i < response.length; i++) {
                    var row = $('<tr>');
                    var cell1 = $('<td>').text(response[i].lsh);
                    var cell2 = $('<td>').text(response[i].barCode);
                    var cell3 = $('<td>').text(response[i].productName);
                    var cell4 = $('<td>').text(response[i].price);
                    var cell5 = $('<td>').text(response[i].count);
                    var cell6 = $('<td>').text(response[i].operator);
                    var cell7 = $('<td>').text(response[i].saleTime);
                    row.append(cell1, cell2, cell3, cell4, cell5, cell6, cell7);
                    table.append(row);
                  }
                  $("#date").val("");
              },
            error: function() {
                alert("查询错误！");
            }
        });
        }
    });
});

/*商品维护*/
//从键盘导入数据
$(document).ready(function () {
    var userName = sessionStorage.getItem("userName");
    // 监听键盘事件
    $(document).keypress(function (e) {
        // 判断是否按下Enter键
        if (e.which == 13) {
            $("#import").click();
        }
    });
    $("#import").click(function (e) { 
        if($("#newBarCode").val() =="" || $("#newProductName").val()=="" || $("#newPrice").val()=="" || $("#newSupply").val()==""){
            alert("请输入对应数据");
        }else{
            $.ajax({
                type: "post",
                url: "Product/maintainFromKeyboard.do", // 服务器端地址（相对路径）
                data: {// key value
                    newBarCode: $("#newBarCode").val(),
                    newProductName: $("#newProductName").val(),
                    newPrice: $("#newPrice").val(),
                    newSupply: $("#newSupply").val(),
                    userName : userName
                },
                dataType: "json",//接收到的数据封装格式
                // 服务器端程序
                success: function (response) {//接收服务器端response响应
                    if (response.code == 1) {// 
                        $("#infoMaintain").text(response.info);
                        $("#newBarCode").val("");
                        $("#newProductName").val("");
                        $("#newPrice").val("");
                        $("#newSupply").val("");

                        setTimeout(function() {
                            $("#infoMaintain").text("");
                        }, 2000);
                    }
                    else if (response.code == 2) {// 失败，显示提示信息
                        // $("#info").val(response.info); // input
                        // $("#info").text(response.info); // span
                        alert(response.info);
                    }
                    else if (response.code == 3) {// 失败，显示提示信息
                        // $("#info").val(response.info); // input
                        // $("#info").text(response.info); // span
                        alert(response.info);
                    }
                    else if (response.code == 4) {// 失败，显示提示信息
                        // $("#info").val(response.info); // input
                        // $("#info").text(response.info); // span
                        alert(response.info);
                    }
                    else if (response.code == 5) {// 失败，显示提示信息
                        // $("#info").val(response.info); // input
                        // $("#info").text(response.info); // span
                        alert(response.info);
                    }
                }
            });
        }

        
    });
});


/*商品维护*/
//从txt导入数据
$(document).ready(function () {

    // 监听上传文件按钮的点击事件
    $('#uploadTxtButton').click(function() {
        var userName = sessionStorage.getItem("userName");
        var txtInput = $('#txtInput')[0];
        var file = txtInput.files[0];
        var fileType = file.type;

        if (fileType != 'text/plain') {
          alert('只能上传txt格式的文件');
          txtInput.value = '';
        } else {
          var formData = new FormData();
          formData.append('file', file);
          formData.append('userName', userName); // 添加userName参数
          $.ajax({
            url: 'Product/maintainFromTxt.do',
            type: "post",
            data:formData,
            processData: false,
            contentType: false,
            dataType: "json",//接收到的数据封装格式
            success: function(response) {
              // 处理返回结果
                if (response.code == 1) {   //成功
                    alert(response.info);
                    txtInput.value = '';
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    alert(response.info);
                }
                else{
                    alert("未知错误");
                }
            },
            error: function() {
                alert('请求失败');
                }
          });
        }
    });
});

/*商品维护 */
/*从Excell导入数据 */
$(document).ready(function () {
    // 监听上传文件按钮的点击事件
    $('#uploadExcelButton').click(function() {
        var userName = sessionStorage.getItem("userName");
        var excelInput = $('#excelInput')[0];
        var file = excelInput.files[0];
        var fileType = file.type;
      
        if (fileType != 'application/vnd.ms-excel' && fileType != 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
          alert('只能上传xls格式的文件！');
          excelInput.value = '';
        } else {
          var formData = new FormData();
          formData.append('file', file);
          formData.append('userName', userName); // 添加userName参数
          $.ajax({
            url: 'Product/maintainFromExcel.do',
            type: "post",
            data:formData,
            processData: false,
            contentType: false,
            dataType: "json",//接收到的数据封装格式
            success: function(response) {
              // 处理返回结果
                if (response.code == 1) {   //成功
                    alert(response.info);
                    excelInput.value = '';
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    alert(response.info);
                }
                else{
                    alert("未知错误");
                }
            },
            error: function() {
                alert('请求失败');
                }
          });
        }
      });
    
});

/*商品维护 */
/*从xml导入数据 */
$(document).ready(function () {
    // 监听上传文件按钮的点击事件
    $('#uploadXmlButton').click(function() {
        var userName = sessionStorage.getItem("userName");
        var xmlInput = $('#xmlInput')[0];
        var file = xmlInput.files[0];
        var fileType = file.type;
      
        if (fileType != 'text/xml' && fileType != 'application/xml') {
          alert('只能上传xml格式的文件');
          xmlInput.value = '';
        } else {
          var formData = new FormData();
          formData.append('file', file);
          formData.append('userName', userName); // 添加userName参数
          $.ajax({
            url: 'Product/maintainFromXml.do',
            type: "post",
            data:formData,
            processData: false,
            contentType: false,
            dataType: "json",//接收到的数据封装格式
            success: function(response) {
              // 处理返回结果
                if (response.code == 1) {   //成功
                    alert(response.info);
                    xmlInput.value = '';
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    alert(response.info);
                }
                else{
                    alert("未知错误");
                }
            },
            error: function() {
                alert('请求失败');
                }
          });
        }
      });
    
});


/*修改密码*/
$(document).ready(function () {
    $("#change").click(function (e) { 
        var userName = sessionStorage.getItem("userName");
        var old_password = $("#old_password").val();    //获取当前密码
        var new_password = $("#new_password").val();    //获取新密码
        var new_password_twice = $("#new_password_twice").val();    //再次获取新密码
        console.log(userName);
        //将用户名和当前密码发送给后端，返回与用户名对应的密码，判断当前密码与返回的密码是否一致
        $.ajax({
            type: "post",
            url: "User/changePassword.do", // 服务器端地址（相对路径）
            data: {// key value
                userName: userName,
                old_password: old_password,
                new_password: new_password,
                new_password_twice: new_password_twice
            },
            dataType: "json",//接收到的数据封装格式
            // 服务器端程序
            success: function(response) {
                if (response.code == 1) {// 成功
                    $("#infoChangePassword").text(response.info);
                    $("#old_password").val("");
                    $("#new_password").val("");
                    $("#new_password_twice").val("");

                    setTimeout(function() {
                        $("#infoChangePassword").text("");
                    }, 2000);
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    alert(response.info);
                }
                else if (response.code == 3) {// 失败，显示提示信息
                    alert(response.info);
                }
                else if (response.code == 4) {// 失败，显示提示信息
                    alert(response.info);
                }
            }

        });
    });
});

//下载销售数据到txt文件
$(document).ready(function() {
    $("#downloadFromTxtBtn").click(function() {
    window.location.href = "Saledetail/DownloadToTxt.do";
    });
});

//下载销售数据到xls文件
$(document).ready(function() {
    $("#downloadFromXlsBtn").click(function() {
    window.location.href = "Saledetail/DownloadToXls.do";
    });
});

//下载销售数据到xml文件
$(document).ready(function() {
    $("#downloadFromXmlBtn").click(function() {
    window.location.href = "Saledetail/DownloadToXml.do";
    });
});

/*注册用户 */
$(document).ready(function () {
    $("#sign").click(function (e) { 
        $.ajax({
            type: "post",
            url: "User/Sign.do", // 服务器端地址（相对路径）
            data: {// key value
                newUserName: $("#newUserName").val(),
                newPassword: $("#newPassword").val(),
                newPassword_twice:$("#newPassword_twice").val(),
                name: $("#name").val(),
                role:$("#role").val()

            },
            dataType: "json",//接收到的数据封装格式
            // 服务器端程序
            success: function (response) {//接收服务器端response响应
                console.log(response);
                if (response.code == 1) {// 成功
                    $("#infoSign").text(response.info); // span
                    $("#newUserName").val("");
                    $("#newPassword").val("");
                    $("#newPassword_twice").val("");
                    $("#name").val("");
                    $("#role").val("");

                    setTimeout(function() {
                        $("#infoSign").text("");
                    }, 2000);
                }
                else if (response.code == 2) {// 失败，显示提示信息
                    // $("#info").val(response.info); // input
                    alert(response.info);
                }
                else if (response.code == 3) {// 失败，显示提示信息
                    alert(response.info);
                }
                else if (response.code == 4) {// 失败，显示提示信息
                    alert(response.info);
                }
            }
        });
    });
});


//点击数据导出按钮跳转到数据导出功能
$(document).ready(function () {
    $("#export").click(function (e) { 
        window.location.href = "export.html";
    });
});


//点击查询统计按钮跳转到查询统计功能
$(document).ready(function () {
    $("#query").click(function (e) { 
        window.location.href = "statistics.html";
    });
});


//点击商品维护按钮跳转到查询统计功能
$(document).ready(function () {
    $("#maintenance").click(function (e) { 
        window.location.href = "maintain.html";
    });
});

//点击修改密码按钮跳转到修改密码功能
$(document).ready(function () {
    $("#changePassword").click(function (e) { 
        window.location.href = "changePassword.html";
    });
});

//点击收银按钮跳转到收银界面 
$(document).ready(function () {
    $("#cashier").click(function (e) { 
        window.location.href = "cashier.html";
    });
});

//点击用户注册按钮跳转到注册界面 
$(document).ready(function () {
    $("#register").click(function (e) { 
        window.location.href = "sign.html";
    });
});

//点击退出按钮，重定向至登录页面
$(document).ready(function () {
    $("#logout").click(function() {
        if (confirm("确定要退出吗？")) {
          window.location.href = "logClass.html"; // 重定向到登录界面
        }
      });
});
