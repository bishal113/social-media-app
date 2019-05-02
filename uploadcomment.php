<?php 
 
 //Constants for database connection
 define('DB_HOST','localhost');
 define('DB_USER','root');
 define('DB_PASS','');
 define('DB_NAME','bishal');
 
 //We will upload files to this folder
 //So one thing don't forget, also create a folder named uploads inside your project folder i.e. MyApi folder
 define('UPLOAD_PATH', 'uploads/');
 
 $log = false;
 //connecting to database 
 $conn = new mysqli(DB_HOST,DB_USER,DB_PASS,DB_NAME) or die('Unable to connect');
 
 
 //An array to display the response
 $response = array();
 
 //if the call is an api call 
 if(isset($_GET['apicall'])){
 
 //switching the api call 
 switch($_GET['apicall']){
 
 //if it is an upload call we will upload the image
 case 'comment':
 
 //first confirming that we have the image and tags in the request parameter
 if(isset($_POST['username'])&&isset($_POST['comments'])&&isset($_POST['image_id'])&&isset($_POST['comments_count'])){
 
 //uploading file and storing it to database as well 
		$stmt = $conn->prepare("INSERT INTO comments (username, comment, image_id) VALUES (?,?,?)");
		$stmt->bind_param("ssi", $_POST['username'],$_POST['comments'],intval($_POST['image_id']));
		$st=$conn->prepare("UPDATE images SET comments=? WHERE ID=?");
		$st->bind_param('ii', intval($_POST['comments_count']), intval($_POST['image_id']));
		if($stmt->execute() && $st->execute()){
		$response['error'] = false;
		$response['message'] = 'comment sent successfully';
		}else{
		$response['error'] = true;
		$response['message'] = 'comment sent failed';
		}
}else{
 $response['error'] = true;
 $response['message'] = "Required params not available";
 }
 
 break;
 
 case 'extractcomment':
  
 //query to get images from database
 $stmt = $conn->prepare("SELECT username, comment, image_id from comments");
 $stmt->execute();
 $stmt->bind_result($username, $comment, $image_id);
 
 $comments = array();
 
 //fetching all the images from database
 //and pushing it to array 
 while($stmt->fetch()){
	 $temp = array();
	 if($image_id == intval($_POST['image_id'])){
		$temp['username']=$username;
		$temp['comment'] = $comment;
		array_push($comments, $temp);
	}
 
 }
 
 //pushing the array in response 
 $response['error'] = false;
 $response['comments'] = $comments; 
 $response['message']= 'Comment received';
 break; 
 
  
 default: 
 $response['error'] = true;
 $response['message'] = 'No Comments Available';
 }
 
 }else{
 header("HTTP/1.0 404 Not Found");
 echo "<h1>404 Not Found</h1>";
 echo "The page that you have requested could not be found.";
 exit();
 }
 
 //displaying the response in json 
 header('Content-Type: application/json');
 echo json_encode($response);
 ?>