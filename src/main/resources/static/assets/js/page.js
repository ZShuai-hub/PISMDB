function page_ctrl(data_obj,data) {
	// 获取操作的对象
  var obj_box=(data_obj.obj_box!== undefined)?data_obj.obj_box:function () {
    return;
  };
  //翻页容器dom对象,必要参数
  var total_item=(data.length!== undefined)?parseInt(Object.keys(data).length):0;//数据条目总数,默认为0,组件将不加载
  console.log(total_item);

  var per_num=(data_obj.per_num!== undefined)?parseInt(data_obj.per_num):2;//每页显示条数,默认为10条

  var current_page=(data_obj.current_page!== undefined)?parseInt(data_obj.current_page):1;//当前页,默认为1

  var total_page=Math.ceil(total_item/per_num);//计算总页数,不足2页,不加载组件

  if(total_page<2){
    return;
  }
  //在指定容器内加载分页插件
  $(obj_box).append('<div class="page_ctrl"></div>');

   //在指定容器内加载分页数据
  $(obj_box).append('<div class="page_content"></div>');

  function page_even() {

    /*加载数据*/

    function change_content() {

    	var target_content='<ul id="contentList">';

    	for(var i = 0;i<per_num;i++)//每页显示的页数。如何调整每页显示的内容刚好对应相应的俄页面
    		{
    			// console.log("当前为"+current_page);
    			target_content+='<li>';
    			target_content+='<div class="item item1"><p class="ID-link">ID: <a href="#"id="ID-link">'+data[per_num*(current_page-1)+i+1].idLink+'</a></p></div>';
    			target_content+='<div class="item item2"><p><a href="#"id="Chemical-name">'+data[per_num*(current_page-1)+i+1].name+'</a></p><div class="chemical-member"><ul><li><p id="basic">Basic Information</p></li><li><p id="pathway">Pathway</p></li><li><p id="related">Related Compounds</p></li><li><p id="supporting">Supporting Information</p></li></ul></div></div>';
    			target_content+='<div class="item item3-t"><ul><li>Protein <span id="Protein">'+data[per_num*(current_page-1)+i+1].basic.protein+'</span></li><li>Organism <span id="Organism">'+data[per_num*(current_page-1)+i+1].organism+'</span></li><li>Uniprot ID <span id="UniprotId">'+data[per_num*(current_page-1)+i+1].basic.uniportID+'</span></li><li>Gene <span id="gene">'+data[per_num*(current_page-1)+i+1].basic.gene+'</span></li><li>Function <span id="function">'+data[per_num*(current_page-1)+i+1].basic.Function+'</span></li></ul></div>';
    			target_content+='</li>';
    		}
    	target_content+='</ul>';
    	$(obj_box).children('.page_content').html(target_content);
    }

    change_content();
    //开始设置page_ctrl
    var append_html='<button class="first_page"><<</button>';
    	append_html+='<button class="prev_page"><</button>';
    for(var i=0;i<total_page;i++){
      if(total_page>8&&current_page>6&&i<current_page-3){
        if(i<2){
          append_html+='<button class="page_num">'+(i+1)+'</button>';
        }
        else if(i==2){
          append_html+='<span class="page_dot">•••</span>';
        }
      }

      else if(total_page>8&&current_page<total_page-3&&i>current_page+1){
        if(current_page>6&&i==current_page+2){
          append_html+='<span class="page_dot">•••</span>';
        }else if(current_page<7){
          if(i<8){
            append_html+='<button class="page_num">'+(i+1)+'</button>';
          }else if(i==8){
            append_html+='<span class="page_dot">•••</span>';
          }
        }
        //append_html+='<span class="page_dot">•••</span>';
      }

      else{
        if(i==current_page-1){
          append_html+='<button class="page_num current_page">'+(i+1)+'</button>';
        }
        else{
          append_html+='<button class="page_num">'+(i+1)+'</button>';
        }
      }
  }
    if(current_page==total_page){
      append_html+='<button class="page_num current_page">'+(i+1)+'</button>';
    }else{
      append_html+='<button class="page_num">'+(i+1)+'</button>';
    }
    append_html+='<button class="next_page">></button><button class="last_page">>></button>';

    // append_html+='<button class="last_page">>></button>';
    $(obj_box).children('.page_ctrl').append(append_html);
    
    if(current_page==1){
      $(obj_box+' .page_ctrl .prev_page').attr('disabled','disabled').addClass('btn_dis');
      $(obj_box+' .page_ctrl .first_page').attr('disabled','disabled').addClass('btn_dis');
    }else{
      $(obj_box+' .page_ctrl .prev_page').removeAttr('disabled').removeClass('btn_dis');
      $(obj_box+' .page_ctrl .first_page').removeAttr('disabled').removeClass('btn_dis');
    }

    if(current_page==total_page){
      $(obj_box+' .page_ctrl .next_page').attr('disabled','disabled').addClass('btn_dis');
      $(obj_box+' .page_ctrl .last_page').attr('disabled','disabled').addClass('btn_dis');
    }else{
      $(obj_box+' .page_ctrl .next_page').removeAttr('disabled').removeClass('btn_dis');
      $(obj_box+' .page_ctrl .last_page').removeAttr('disabled').removeClass('btn_dis');
    }
  }

  page_even();
  $(obj_box+' .page_ctrl').on('click','button',function () {
    var that=$(this);
    if(that.hasClass('prev_page')){
      if(current_page!=1){
        current_page--;
        that.parent('.page_ctrl').html('');
        page_even();
      }
    }
    else if(that.hasClass('first_page')){
    	if(current_page!=1){
    		current_page=1;
    		that.parent('.page_ctrl').html('');
    		page_even();	
    	}
    	
    }

    else if(that.hasClass('next_page')){
      if(current_page!=total_page){
        current_page++;
        that.parent('.page_ctrl').html('');
        page_even();
      }
    }
    else if(that.hasClass('last_page')){
    		current_page=total_page;
    		 that.parent('.page_ctrl').html('');
    		page_even();
    }
    else if(that.hasClass('page_num')&&!that.hasClass('current_page')){
      current_page=parseInt(that.html());
      that.parent('.page_ctrl').html('');
      page_even();
    }
  });
  // 接下来一个问题就是处理每一个li中的item3中的内容
}

