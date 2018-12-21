<?php
require 'init.php';
header("Content-Type: application/json; charset=UTF-8");
$dateNow = new DateTime();
$date_now = $dateNow->format('Y-m-d H:i:s');
$date_now_strtotime =  strtotime($date_now)."<br>";

if (isset($_GET['all'])) {

    $query = $conn->prepare("SELECT * FROM parking_active where parking_active_dateOut >= ? ORDER BY parking_active_dateOut ASC");
    $query->bind_param("s", $date_now);
    $query->execute();
    $result = $query->get_result();
    if ($result->num_rows < 0) exit(json_encode(array('success' => false, 'data' => "Receiving data failed! Try again")));
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $parking_active_id = $row['id_parking_active'];
        $user_id = $row['id_user'];
        $parking_id = $row['id_parking'];
        $parking_active_carID = $row['parking_active_carID'];
        $parking_active_dateIn = $row['parking_active_dateIn'];
        $parking_active_dateOut = $row['parking_active_dateOut'];
        $parking_active_confirmationCode = $row['parking_active_confirmationCode'];

        $dateIN = new DateTime($parking_active_dateIn);
        $dateOUT = new DateTime($parking_active_dateOut);
        $since_start = $dateNow->diff($dateOUT);
        $minutes_left = $since_start->i;

        array_push($data, array('parking_active_id' => $parking_active_id,
            'user_id' => $user_id,
            'parking_id' => $parking_id,
            'parking_active_carID' => $parking_active_carID,
            'parking_active_dateIn' => $parking_active_dateIn,
            'parking_active_dateOut' => $parking_active_dateOut,
            'parking_active_confirmationCode' => $parking_active_confirmationCode,
            'minutes_left' => $minutes_left
        ));
    }
    echo json_encode(array('success' => true, "data" => $data));
    $query->close();
    $conn->close();
}

if (isset($_GET['user'])) {

    $user_id = mysqli_real_escape_string($conn,$_GET['user']);

    $query = $conn->prepare("SELECT * FROM parking_active where id_user = ? and parking_active_dateOut >= ? ");
    $query->bind_param("is", $user_id,$date_now);
    $query->execute();
    $result = $query->get_result();
    if ($result->num_rows < 1) exit(json_encode(array('success' => false, 'data' => "Receiving data failed! Try again")));
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $id_parking_active = $row['id_parking_active'];
        $id_user = $row['id_user'];
        $parking_id = $row['id_parking'];
        $parking_active_carID = $row['parking_active_carID'];
        $parking_active_dateIn = $row['parking_active_dateIn'];
        $parking_active_dateOut = $row['parking_active_dateOut'];
        $parking_active_confirmationCode = $row['parking_active_confirmationCode'];

        $dateIN = new DateTime($parking_active_dateIn);
        $dateOUT = new DateTime($parking_active_dateOut);
        $since_start = $dateNow->diff($dateOUT);
        $minutes_left = $since_start->i;

        array_push($data, array('parking_active_id' => $parking_active_id,
            'user_id' => $user_id,
            'parking_id' => $parking_id,
            'parking_active_carID' => $parking_active_carID,
            'parking_active_dateIn' => $parking_active_dateIn,
            'parking_active_dateOut' => $parking_active_dateOut,
            'parking_active_confirmationCode' => $parking_active_confirmationCode,
            'minutes_left' => $minutes_left
        ));
    }
    echo json_encode(array('success' => true, "data" => $data));
    $query->close();
    $conn->close();
}

if (isset($_POST['data'])) {
    $random_hash = substr(md5(uniqid(                                             $date_now_strtotime, true)), 16, 16);

    # Get JSON as an object
    $json_obj = json_decode($_POST['data']);

    $id_parking = $json_obj->{'id_parking'};
    $id_user = $json_obj->{'id_user'};
    $carID = $json_obj->{'carID'};


    //Number of active and space on Parking
    $query = $conn->prepare("SELECT p.id_parking,p.parking_space, COUNT(pa.id_parking) AS Active FROM parking p LEFT JOIN parking_active pa ON (pa.id_parking=p.id_parking AND pa.parking_active_dateOut >= ?) WHERE p.id_parking = ? GROUP BY p.id_parking");
    $query->bind_param('si',$date_now,$id_parking);
    $query->execute();
    $result = $query->get_result();
    if ($result->num_rows < 1) exit(json_encode(array('success' => false, 'data' => "Receiving data failed! Try again")));
    $result_parking_id = "";
    $result_parking_space = "";
    $result_active = "";
    while ($row = $result->fetch_assoc()) {
        $result_parking_id = $row['id_parking'];
        $result_parking_space = $row['parking_space'];
        $result_parking_space = $row['parking_space'];
        $result_active = $row['Active'];

    }
    if($result_active < $result_parking_space) {

        $date_out = strtotime($date_now);
        $date_out = strtotime("+1 hour", $date_out);
        $date_out = date('Y-m-d H:i:s', $date_out);

        $stmt = $conn->prepare("INSERT INTO `parking_active` (`id_parking_active`, `id_user`, `id_parking`, `parking_active_carID`, `parking_active_dateIn`, `parking_active_dateOut`, `parking_active_confirmationCode`) 
        VALUES (NULL, ?, ?, ?, ?, ?, ?);");
        $stmt->bind_param("iissss", $id_user, $id_parking, $carID, $date_now, $date_out, $random_hash);
        $rc = $stmt->execute();

        if (false === $rc) {
            echo json_encode(array('success' => false, 'data' => $stmt->error));
            die();
        }

        echo json_encode(array('success' => true, 'confirmation_code' => $random_hash));

        $stmt->close();
        $conn->close();
    }else{
        echo json_encode(array('success' => true, 'confirmation_code' => "No Space"));
    }

}
?>

