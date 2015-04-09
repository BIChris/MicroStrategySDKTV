$(function() {
	 $(".customGroup").click(function() {
         //show groups
		 $('#projctGroupBack').toggle();
		 var childrenToToggle = $('.custProj');
		 
		 for (i = 0; i < childrenToToggle.length; i++) { 
			    if($(childrenToToggle[i]).attr('grpId') == $(this).attr('parentGrpId')){
			    	$(childrenToToggle[i]).removeClass("hidden");
			    } 
			}
		 
		 //hide group selector parent
		 $('#groupParent').toggle();
     });
	 
	 $("#projctGroupBack").click(function() {
		 $('#projctGroupBack').toggle();
		 
		 var childrenToToggle = $(".custProj[class='custProj mstrProjectItem']");
		 
		 for (i = 0; i < childrenToToggle.length; i++) { 
			    
			    	$(childrenToToggle[i]).addClass("hidden");
			   
			}
		 //show group selector parent
		 $('#groupParent').toggle();
	 });
	 
});