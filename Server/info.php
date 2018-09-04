<?php
header('Access-Control-Allow-Origin:*');
header("Content-type:text/html;charset=utf-8");
$info_content = file_get_contents("php://input");
$info_JSON = json_decode($info_content, true);
$qrcode = file_get_contents("qrcode.json");
$code_JSON = json_decode($qrcode, true);
if ($info_JSON->randkey == $code_JSON->randkey and $info_JSON->hostname == $code_JSON->hostname) {
    $file = fopen("info.json", "w") or die("Unable to open file!");
    fwrite($file, $info_content);
    fclose($file);
}
?>
