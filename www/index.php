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
		case 'docs':
			$pageFileName = 'docs/index.html';
			break;
		case 'home':
			$pageFileName = 'home.html';
			break;
		case 'cvs':
			$pageFileName = 'http://cvs.sourceforge.net/viewcvs.py/dbcopyplugin';
			break;
		case 'screenshots':
			$pageFileName = 'screenshots.html';
			break;
		case 'faq':
			$pageFileName = 'faq.html';
			break;
		case 'downloads':
			$pageFileName = 'http://sourceforge.net/project/showfiles.php?group_id=141843';
			break;
		case 'bugs':
			$pageFileName = 'http://sourceforge.net/tracker/?group_id=141843&atid=750797';
			break;
		case 'datatypemappings':
			$pageFileName = 'dbcopydatatypemapping.html';
			break;
		case 'changelog':
			$pageFileName = 'changelog.html';
			break;
		case 'preferences':
			$pageFileName = 'docs/preferences.html';
			break;
		case 'usage':
			$pageFileName = 'docs/readme.html';
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

