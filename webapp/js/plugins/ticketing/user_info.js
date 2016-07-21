$(function(){ $(".info_user").popover(
    { 
      trigger: "manual",
      html: true, 
      animation:false,
      content: function() {
    		return $("#template-"+$(this).attr("data-key-id")).html();
  		}
    }
  )
  .on("mouseenter", function () {
        var _this = this;
        $(this).popover("show");
        $(".popover").on("mouseleave", function () {
            $(_this).popover('hide');
        });
    }).on("mouseleave", function () {
        var _this = this;
        setTimeout(function () {
            if (!$(".popover:hover").length) {
                $(_this).popover("hide");
            }
        }, 300);
  });
});