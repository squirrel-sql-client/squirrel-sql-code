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
	$page = $_GET['page'];
	if (is_null($page))
	{
		$page = 'home.html';
	}
	if (ereg('.txt$', $page))
	{
		echo("<PRE>");
	}
	include $page;
	if (ereg('.txt$', $page))
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

