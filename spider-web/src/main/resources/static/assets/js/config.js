function post(url, data, successfn, errorfn) {
    data = (data == null || data == "" || typeof(data) == "undefined") ? {"date": new Date().getTime()} : data;
    $.ajax({
        type: "post",
        data: data,
        url: url,
        dataType: "json",
        success: function (d) {
            if (d.status) {
                if(!d.status && '10001' == d.statusCode){//用户未登陆
                    window.location.href = "login.html";
                }
                successfn(d);
            } else {
                errorfn(d);
            }
        },
        error: function (e) {
            alert(e);
        }
    });
};


function postAjax(url, data, successfn) {
    data = (data == null || data == "" || typeof(data) == "undefined") ? {"date": new Date().getTime()} : data;
    $.ajax({
        type: "post",
        data: data,
        url: url,
        dataType: "json",
        success: function (d) {
            if(!d.status && '10001' == d.statusCode){//用户未登陆
                window.location.href = "login.html";
            }
            successfn(d);
        },
        error: function (e) {
            alert(e);
        }
    });
};

function postAsync(url, data, successfn){
    data = (data == null || data == "" || typeof(data) == "undefined") ? {"date": new Date().getTime()} : data;
    $.ajax({
        type: "post",
        data: data,
        url: url,
        async: false,
        dataType: "json",
        success: function (d) {
            if(!d.status && '10001' == d.statusCode){//用户未登陆
                window.location.href = "login.html";
            }
            successfn(d);
        },
        error: function (e) {
            alert(e);
        }
    });
}


function getAjax(url, data, successfn) {
    data = (data == null || data == "" || typeof(data) == "undefined") ? {"date": new Date().getTime()} : data;
    $.ajax({
        type: "get",
        data: data,
        url: url,
        dataType: "json",
        success: function (d) {
            if(!d.status && '10001' == d.statusCode){//用户未登陆
                window.location.href = "login.html";
            }
            successfn(d);
        },
        error: function (e) {
            alert(e);
        }
    });
};

/**
 * 进度条
 */
function showLoadingDialog(){
    swal({
        title: "请稍等......",
        html: true,
        allowOutsideClick:false,
        text: "<div class=\"progress progress-striped\" ><div class=\"progress-bar progress-bar-success\" style=\"width: 100%;\"></div> </div>",
        showConfirmButton: false
    });
}


function getDicData(root,dicType){
    var json = getDicJsonData(root,dicType);
    return dicJsonToObj(json);
}

function getDicJsonData(root,dicType){
    var json;
    postAsync(root+$.URL.dic.getDicByType,{'dicType':dicType},function(result){
        json = result.data;
    });
    return json;
}

function dicJsonToObj(json){
    var o = {};
    $.each(json,function(){
        o[this.dicKey] = this.dicValue;
    });
    return o;
}