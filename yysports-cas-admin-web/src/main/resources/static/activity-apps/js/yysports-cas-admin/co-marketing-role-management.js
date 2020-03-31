

yy.lib.render($("#yyadmin_content_body"), _ctx.module, null, function () {
    if (_ctx.module == "coachAdd") {
        yy.api.getCourse(yy.lib.getParameter('id'), function (data) {
            if (data.body != null) {
                detailVal(data.body);
            }

        });
        $("#basicSumbit").click(function () {

            if (yy.lib.getParameter('id') > 0) {
                let fitnessCoach = getVal1();
                fitnessCoach.id = yy.lib.getParameter('id')
                if (mobileReg.test($("[name=phone]").val()) == false) {
                    alert("请输入有效的手机号");
                    return false;
                }
                yy.api.editCourse(fitnessCoach, function (data) {
                    if (data.status) {

                        alert("教练編輯成功！");
                        location.href = _ctx.systemAddress + '/_page/coach-info-management/index';
                    } else {
                        //alert(data.msg);
                        let _msg = $('[name=' + data.msg.split(/\s+/)[1] + ']').attr("placeholder");
                        alert(_msg);
                    }
                })
            }
            else {
                let fitnessCoach = getVal1();
                // if($("input[name=headIcon]").val().trim()=="")
                // {
                //     alert("请上传教练头像");
                //     return false;
                // }
                // if($("input[name=name]").val().trim()=="")
                // {
                //     alert("请输入姓名");
                //     return false;
                // }
                if (mobileReg.test($("[name=phone]").val()) == false) {
                    alert("请输入有效的手机号");
                    return false;
                }
                yy.api.createCoach(fitnessCoach, function (data) {
                    if (data.status) {

                        alert("教练添加成功！");
                        location.href = _ctx.systemAddress + '/_page/coach-info-management/index';
                    } else {
                        let _msg = $('[name=' + data.msg.split(/\s+/)[1] + ']').attr("placeholder");
                        alert(_msg);
                        //alert(data.msg);
                    }
                })
            }


        })
    }
    if (_ctx.module == "coachDetail") {

        yy.api.getCourse(yy.lib.getParameter('id'), function (data) {
            detailVal(data.body);
            if ($("[name=gender]").text() == "1") {
                $("[name=gender]").text("女")
            }
            else {
                $("[name=gender]").text("男")
            }

        });

        //detailVal();
    }
    if (_ctx.module == "index") {

        yy.app.CoachlistConfig = {
            "sidColumn": "id",
            "header": {
                "name": "角色名称",
                "desc": "描述",
                "createTime": "创建时间",
                "operation": "操作"
            },
            "dateColumn": {
                "createdAt": "short" //cn， short, slash
            },
            "needsPagination": true,
            "queryParams": {
                "pageNum": "1",
                "pageSize": "20"
            },
            "columnHandler": {
            	/*
                "enabled": function (val, row) {
                    if ((val == 0)) {
                        let html = '<a data-type=' + val + ' data-id=' + row.id + ' href="javascript:;" onclick="enabledType(this);">已停用</a>';
                        return html;
                    } else {
                        let html = '<a data-type=' + val + ' data-id=' + row.id + ' href="javascript:;" onclick="enabledType(this);">啟用</a>';
                        return html;
                    }
                }
                */
            },
            "operationColumn": {
                "Edit": {
                    "text": "編輯",
                    "resourceId": "SetoutStrategyProduct_EditProduct",
                    "event": function (id) {
                        // var newCardName = yy.app.listConfig.dataLoaded[sid].cardName;
                        // var newCardId = yy.app.listConfig.dataLoaded[sid].cardId;
                        //alert("sdf");
                        window.location.href = _ctx.systemAddress + '/_page/co-marketing-role-management/coachAdd.html?id=' + id;
                    }
                },
                "Delete": {
                    "text": " 刪除",
                    "resourceId": "SetoutStrategyProduct_EditProduct",
                    "event": function (id) {
                        // var newCardName = yy.app.listConfig.dataLoaded[sid].cardName;
                        // var newCardId = yy.app.listConfig.dataLoaded[sid].cardId;
                        //alert("sdf");
                    	if (window.confirm("請再次確認是否刪除？")) {
                            yy.api.deleteCenter(id, function (data) {

                                if (data.status) {
                                    alert("刪除成功");
                                    window.location.href = _ctx.systemAddress + '/_page/co-marketing-role-management/index';
                                }
                                else {
                                    alert(data.msg);
                                }

                            })
                    	}
                    }
                }
            }
        };
        yy.engine.loadList();
    }
})
var coach = {};

//设值coach.fitnessCoach={}
function getVal() {
    let _from = $("form").serializeArray();
    let fitnessCoach = new Object();
    $(_from).each(function (index, el) {
        fitnessCoach[el.name] = el.value;
    })
    fitnessCoach.describe= yy.app.editor1.um.getContent();

    coach.fitnessCoach = fitnessCoach;
    return coach;
}
//设值coach={}
function getVal1() {
    let _from = $("form").serializeArray();
    let fitnessCoach = new Object();
    $(_from).each(function (index, el) {
        coach[el.name] = el.value;
    })
    coach.describe= yy.app.editor1.um.getContent();
    return coach;
}

//页面赋值
function detailVal(data) {
    let coach = data;

    Object.keys(coach).forEach(v => {

        $('[name=' + v + ']').each(function (index, el) {
            if ($(el)[0].type == "radio") {
                $("input:radio[name=" + v + "][value=" + coach[v] + "]").attr("checked", true);
            }
            if ($(el)[0].tagName == "INPUT") {
                if ($(el)[0].type == "text") {
                    $(el).val(coach[v]);
                }
                if ($(el)[0].type == "radio") {
                    $("input:radio[name=" + v + "][value=" + coach[v] + "]").attr("checked", true);
                }
                if ($(el)[0].type == "hidden") {
                    $(el).val(coach[v]);
                }
                // if($('[name='+v+']')[0].type=="radio")
                // {
                //     $("input:radio[name="+v+"][value="+coach[v]+"]").attr("checked",true);  
                // }
            }
            if ($(el)[0].tagName == "IMG") {
                if (coach[v].length > 10) {
                    $(el).css("opacity", 1).attr("src", coach[v]);
                }
            }
            if ($(el)[0].tagName == "LABEL") {
                $(el).text(coach[v]);
            }
            if ($(el)[0].tagName == "SELECT") {
                $(el).val(coach[v]);
            }
            if ($(el)[0].tagName == "TEXTAREA") {
                $(el).val(coach[v]);
            }
            if(v=="describe"&&coach[v]!=null)
            {
                //编辑器内容0
                if(typeof(yy.app.editor1)!="undefined" )
                {
                    yy.app.editor1.um.setContent(coach[v]);
                }else
                {
                    $('[name=' + v + ']').html(coach[v])
                }
                
            }
        })
    })

}