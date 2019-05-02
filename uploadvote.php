<?php 
 
 //Constants for database connection
 define('DB_HOST','localhost');
 define('DB_USER','root');
 define('DB_PASS','');
 define('DB_NAME','bishal');
 
 //We will upload files to this folder
 //So one thing don't forget, also create a folder named uploads inside your project folder i.e. MyApi folder
 
 
 //connecting to database 
 $conn = new mysqli(DB_HOST,DB_USER,DB_PASS,DB_NAME) or die('Unable to connect');
 
 
 //An array to display the response
 $response = array();
 
 //if the call is an api call 
 if(isset($_GET['apicall'])){
 
 //switching the api call 
 switch($_GET['apicall']){

 case 'agree':
 
 if(isset($_POST['agrees']) && isset($_POST['id']) && isset($_POST['user_id'])){

		 try{
			 
				 $agree= intval($_POST['agrees']);
				 $id= intval($_POST['id']);
				 $userid = intval($_POST['user_id']);

				 //mysqli_query($conn,"INSERT INTO images (agrees) SET agrees=$agree WHERE ID=$id");
				 $stmt = $conn->prepare("UPDATE images SET agrees=? where id=?");
				 $stmt->bind_param('ii',$agree,$id);
				 
				 $st = $conn->prepare("INSERT INTO likes (user_id,image_id) value(?,?)");
				 $st->bind_param("ii",$userid,$id);
				 
				 if($stmt->execute() && $st->execute()){	 
					 $response['error'] = false;
					 $response['message'] = 'Vote Updated sucessfully';
				 }
				 else{
					 $response['error'] = true;
					 $response['message'] = 'Vote not updated';
				 }
		 }catch(Exception $e){
				 $response['error'] = true;
				 $response['message'] = 'Could not update';
		 }
 
 }else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
 }
 
 break;
 
 default: 
 $response['error'] = true;
 $response['message'] = 'Invalid api call';
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