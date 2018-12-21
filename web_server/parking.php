<?php
header("Content-Type: application/json; charset=UTF-8");
require 'init.php';
$dateNow = new DateTime();
$date_now = $dateNow->format('Y-m-d H:i:s');

if (isset($_GET['byZone'])) {

    $zone = mysqli_real_escape_string($conn,$_GET['byZone']);
    $query = $conn->prepare("SELECT p.id_parking,p.parking_name,p.parking_space,p.parking_address,p.parking_work_time,p.parking_price,p.parking_description,p.parking_latitude,p.parking_longitude,p.id_zone, COUNT(pa.id_parking) AS Active FROM parking p LEFT JOIN parking_active pa ON (pa.id_parking=p.id_parking AND pa.parking_active_dateOut >= ?) WHERE p.id_zone = ? GROUP BY p.id_parking");
    $query->bind_param("si", $date_now,$zone);
    $query->execute();
    $result = $query->get_result();
    if ($result->num_rows < 1) exit(json_encode(array('success' => false, 'data' => "Receiving data failed! Try again")));
    $data = array();
    while ($row = $result->fetch_assoc()) {
         $parking_id = $row['id_parking'];
         $parking_name = $row['parking_name'];
         $parking_space = $row['parking_space'];
         $parking_address = $row['parking_address'];
         $parking_work_time = $row['parking_work_time'];
         $parking_price = $row['parking_price'];
         $parking_description = $row['parking_description'];
         $parking_latitude = $row['parking_latitude'];
         $parking_longitude = $row['parking_longitude'];
         $parking_zone = $row['id_zone'];
        $active = $row['Active'];

        array_push($data, array('parking_id' => $parking_id,
            'parking_name' => $parking_name,
            'parking_space' => $parking_space,
            'parking_address' => $parking_address,
            'parking_work_time' => $parking_work_time,
            'parking_price' => $parking_price,
            'parking_description' => $parking_description,
            'parking_latitude' => $parking_latitude,
            'parking_longitude' => $parking_longitude,
            'parking_zone' => $parking_zone,
            'active' => $active
        ));
    }

    echo json_encode(array('success' => true, "data" => $data));

    $query->close();
    $conn->close();
}


if (isset($_GET['all'])) {

    $query = $conn->prepare("SELECT p.id_parking,p.parking_name,p.parking_space,p.parking_address,p.parking_work_time,p.parking_price,p.parking_description,p.parking_latitude,p.parking_longitude,p.id_zone, COUNT(pa.id_parking) AS Active FROM parking p LEFT JOIN parking_active pa ON (pa.id_parking=p.id_parking AND pa.parking_active_dateOut >= ?) GROUP BY p.id_parking");
    $query->bind_param('s',$date_now);
    $query->execute();

    $result = $query->get_result();
    if ($result->num_rows < 1) exit(json_encode(array('success' => false, 'data' => "Receiving data failed! Try again")));
    $data = array();
    while ($row = $result->fetch_assoc()) {
        $parking_id = $row['id_parking'];
        $parking_name = $row['parking_name'];
        $parking_space = $row['parking_space'];
        $parking_address = $row['parking_address'];
        $parking_work_time = $row['parking_work_time'];
        $parking_price = $row['parking_price'];
        $parking_description = $row['parking_description'];
        $parking_latitude = $row['parking_latitude'];
        $parking_longitude = $row['parking_longitude'];
        $parking_zone = $row['id_zone'];
        $active = $row['Active'];

        array_push($data, array('parking_id' => $parking_id,
            'parking_name' => $parking_name,
            'parking_space' => $parking_space,
            'parking_address' => $parking_address,
            'parking_work_time' => $parking_work_time,
            'parking_price' => $parking_price,
            'parking_description' => $parking_description,
            'parking_latitude' => $parking_latitude,
            'parking_longitude' => $parking_longitude,
            'parking_zone' => $parking_zone,
            'active' => $active
        ));
    }
    echo json_encode(array('success' => true, "data" => $data));

    $query->close();
    $conn->close();
}

?>