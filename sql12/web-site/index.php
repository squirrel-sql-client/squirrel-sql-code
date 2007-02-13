<html>

<?php include "meta.html"; ?>

<body>

<?php include "header.html"; ?>

<div align="left">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td width="220" valign="top">
<?php include "left.html"; ?>
			</td>
			<td width="20">&nbsp;&nbsp;</td>
			<td width="100%" valign="top">
<?php
	$pageKey = $_GET['page'];
	$pageFileName = '';
	if (is_null($pageKey))
	{
		$pageKey = 'home';
	}

	switch ($pageKey)
	{
		case 'tutorial':
			$pageFileName = 'kulvir/tutorial.html';
			break;
		case 'home':
			$pageFileName = 'home.html';
			break;
		case 'plugins':
			$pageFileName = 'plugins.html';
			break;
		case 'translations':
			$pageFileName = 'translations.html';
			break;
		case 'screenshots':
			$pageFileName = 'screenshots.html';
			break;
		case 'programing':
			$pageFileName = 'programming.html';
			break;
		case 'faq':
			$pageFileName = 'faq.html';
			break;
		case 'old':
			$pageFileName = 'old.html';
			break;
		case 'changes':
			$pageFileName = 'latest-changes.html';
			break;
	    case 'yourkit':
	    	$pageFileName = 'yourkit.html';
	    	break;
		default:
			$pageFileName = 'home.html';
			break;
	}
	
	if (ereg('.txt$', $pageFileName))
	{
		echo("<PRE>");
	}
	include $pageFileName;
	if (ereg('.txt$', $pageFileName))
	{
		echo("</PRE>");
	}
?>
			</td>
		</tr>
	</table>
</div>
<table border="0"cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
<?php include "footer.html"; ?>
		</td>
	</tr>
</table>

</body>
</html>

