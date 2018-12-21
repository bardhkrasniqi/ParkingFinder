<?php
header("Content-Type: application/json; charset=UTF-8");
require 'init.php';

if(isset($_GET['email']) && isset($_GET['password'])){
$email = mysqli_real_escape_string($conn,$_GET['email']);
$password = mysqli_real_escape_string($conn,$_GET['password']);
$query = $conn->prepare("SELECT * FROM users where user_email = ?");
$query->bind_param("s", $email);
$query->execute();
$result = $query->get_result();
if($result->num_rows != 1) exit(json_encode(array('success' => false,'data'=>"Login failed! Try again")));
$data;
while($row = $result->fetch_assoc()) {
    $user_id = $row['id_user'];
    $user_fullname = $row['user_fullname'];
    $user_car = $row['user_car_identification'];
    $user_email = $row['user_email'];
    $user_salt1 = $row['user_salt'];
    $user_password = $row['user_password'];
    $user_role = $row['id_user_role'];
    $data = array('id_user'=>$user_id,'user_fullname'=>$user_fullname,'user_car_identification'=>$user_car,'user_email'=>$user_email,'id_user_role'=>$user_role);
}

    $salt2 = 'pg!@`';
    $token = hash('ripemd128',"$user_salt1$password$salt2");

    if($token === $user_password){
        echo json_encode(array('success'=>true,"data"=>$data));
    }else{
        echo json_encode(array('success' => false,'data'=>"Login failed! Try again"));
        die();
    }


$query->close();
$conn->close();
}else{
    echo json_encode(array('success' => false,'data'=>"Parameters are not right, contact support"));
}
?>