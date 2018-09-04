<?php
header('Access-Control-Allow-Origin:*');
header("Content-type:text/html;charset=utf-8");
$code_content = file_get_contents("php://input");
$qrcode = fopen("qrcode.json", "w") or die("Unable to open file!");
fwrite($qrcode, $code_content);
fclose($qrcode);
?>
