/**
 * Created by liumengjun on 1/15/16.
 */
$(function(){
    var getObjectURL = function(file) {
      var url = null;
      if (window.createObjectURL != undefined) {
        url = window.createObjectURL(file);
      } else if (window.URL != undefined) {
        url = window.URL.createObjectURL(file);
      } else if (window.webkitURL != undefined) {
        url = window.webkitURL.createObjectURL(file);
      }
      return url;
    };
    // input file changed event
    $('input[type=file]').change(function(e){
        var ele = e.target, $ele = $(ele);
        var $uploadBox = $ele.closest(".img-upload-box");
        var imgType = ["gif", "jpeg", "jpg", "bmp", "png"];
        var flag = validImgFile();
        if (!flag) {
          return false;
        }
        var imtUrl = getObjectURL(ele.files[0]);
        var $previewBox = $uploadBox.find(".img-preview-box");
        $previewBox.find('img').attr("src", imtUrl);
        $previewBox.show();
        return true;

        // valid image properties
        function validImgFile() {
          if (!ele.value || !ele.files) {
            return false;
          }
          //验证上传文件格式是否正确
          if (!RegExp("\.(" + imgType.join("|") + ")$", "i").test(ele.value.toLowerCase())) {
            $uploadBox.addClass('has-error');
            $uploadBox.find('.help-block').text('选择图片类型错误');
            this.value = "";
            return false;
          }
          return true;
        }
    });
    // 工具提示
    $(function () {
        $('[data-toggle="tooltip"]').tooltip({'html':true})
    });
    // 其他认证列表,[编辑][删除]按钮
    $("#otherCertsList [data-action=edit-cert]").click(function (e){
        var $row = $(this).closest('.row');
        var certId = $row.attr('certId'), certName = $row.find('.cert-name').text(), certImgUrl = $row.find('.cert-img-view img')[0].src;
        var $form = $('#certEditForm');
        $form.find('input[name=id]').val(certId);
        $form.find('input[name=name]').val(certName);
        $form.find('.img-box img')[0].src = certImgUrl;
    });
    $("#otherCertsList [data-action=delete-cert]").click(function (e){
        var decided = confirm('确定要删除这个证书吗?');
        if (!decided) return false;
    });
    //form取消操作
    $("#certEditForm .btn-cancel").click(function(e){
        var $form = $('#certEditForm');
        $form[0].reset();
        $form.find('.img-preview-box img')[0].src = '';
        if ($form.find('input[name=id]')[0]) { // 其他认证页面,需要清空图片
            $form.find('.img-box img')[0].src = '';
        }
    });
});
