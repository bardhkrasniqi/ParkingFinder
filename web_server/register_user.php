<?php
header("Content-Type: application/json; charset=UTF-8");
require 'init.php';

if (isset($_POST['data'])) {

    $characters = '!@#%&@012345`6789abcdefghijklmnop`qrstuvwxyz!@#%^&`()-_+=ABCDEFGHIJKL`MNOPQRSTUVWXYZ';
    $salt1 = '';
    for ($i = 0; $i < 10; $i++) {
        $salt1 .= $characters[rand(0, strlen($characters))];
    }

    # Get as an object
    $json_obj = json_decode($_POST['data']);

    $user_fullname = $json_obj->{'fullname'};
    $user_car = $json_obj->{'car'};
    $user_email = $json_obj->{'email'};
    $user_password = $json_obj->{'password'};

    $salt2 = 'pg!@`';
    $token = hash('ripemd128',"$salt1$user_password$salt2");

    $stmt = $conn->prepare("INSERT INTO `users` (`id_user`, `user_fullname`, `user_car_identification`, `user_email`,  `user_password`, `user_salt`, `id_user_role`) VALUES (NULL, ?, ?, ?, ?, ?, '3');");
    $stmt->bind_param("sssss", $user_fullname, $user_car, $user_email,$token,$salt1);
    $rc = $stmt->execute();

    if ( false===$rc ) {
        echo json_encode(array('success' => false,'data'=>$stmt->error));
        die();
    }
    echo json_encode(array('success' => true,'data'=>"Now you are part of our journey"));

    $stmt->close();
    $conn->close();
}

?>