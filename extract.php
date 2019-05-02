<?php
	define('DB_HOST','localhost');
	define('DB_USER','root');
	define('DB_PASS','password');
	define('DB_NAME','bishal');
	$server_ip = gethostbyname(gethostname());

	$conn = new mysqli(DB_HOST,DB_USER,DB_PASS,DB_NAME) or die('Unable ro connect');

	$stmt = $conn->prepare("SELECT id,image,title from images");
	$stmt->execute();
	$stmt->bind_result($id,$image,$title);

	$images = array();

	while($stmt->fetch()){
		$temp = array();
		$temp['id'] = $id;
		$temp['image'] = 'http://'.$server_ip.'/androiduploads/'.UPLOAD_PATH.$image;

		array_push($images,$temp);
	}

	$response['error'] = false;
	$response['message'] = '$images';

	echo = json_encode($response);
?>
