// Apparently click is better chan change? Cuz IE?
$(document).ready(function() {

$('input[type="checkbox"]').change(function(e) {

  var checked = $(this).prop("checked"),
      container = $(this).parent(),
      siblings = container.siblings();

  container.find('input[type="checkbox"]').prop({
    indeterminate: false,
    checked: checked
  });

  function checkSiblings(el) {

    var parent = el.parent().parent(),
        all = true;

    el.siblings().each(function() {
      return all = ($(this).children('input[type="checkbox"]').prop("checked") === checked);
});

if (all && checked) {

  parent.children('input[type="checkbox"]').prop({
    indeterminate: false,
    checked: checked
  });

  checkSiblings(parent);

} else if (all && !checked) {

  parent.children('input[type="checkbox"]').prop("checked", checked);
  parent.children('input[type="checkbox"]').prop("indeterminate", (parent.find('input[type="checkbox"]:checked').length > 0));
  checkSiblings(parent);

} else {

  el.parents("li").children('input[type="checkbox"]').prop({
        indeterminate: true,
        checked: false
      });

    }

  }

  checkSiblings(container);
});
$('input[type="checkbox"]').change(writeCheckedTextboxes);

loadSavedData();
})

function loadSavedData() {
	var hiddenInput = $('input[type="hidden"]').val();
	var checkedDishCategories = hiddenInput.split("   ");
	checkedDishCategories.forEach(function(elem) 
			{
				$("input[id='" + elem + "']").prop("checked", true);
			});
}

function writeCheckedTextboxes() {
	// Récupération de la textbox
	$textField = $("#dishCategoriesText");
	// On la vide
	$textField.val("");
	
	$.each($(".dish-categories li input"), function(index, elem) 
			{
				if ($(elem).prop("checked")) {
					// Si le checkbox est cochée, on ajoute l'identifiant de celle ci à la textbox
					$textField.val($textField.val() + $(elem).prop("id") + "   ");
				}
			});
}