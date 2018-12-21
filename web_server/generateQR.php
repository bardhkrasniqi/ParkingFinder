<?php
header("Content-Type:image/png");
require "vendor/autoload.php";
use Endroid\QrCode\QrCode;
$str = $_GET['text'];
//Encode text
$b64_str =  base64_encode($str);
// Create a basic QR code
$qrCode = new QrCode($b64_str);
echo $qrCode->writeString();
die();
?>