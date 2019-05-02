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
 case 'register':
 
 //first confirming that we have the image and tags in the request parameter
 if(isset($_POST['email'])&&isset($_POST['password'])&&isset($_POST['conf_password'])&&isset($_POST['name'])&&isset($_POST['phno'])){
 
 //uploading file and storing it to database as well 
 try{
	 $stmt = $conn->prepare("SELECT username, email  FROM auth");
	 if($stmt->execute()){
		$stmt->bind_result($username, $email);
			 //fetching all the images from database
		 //and pushing it to array 
			 while($stmt->fetch()){
				 if($_POST['email']==$email || $_POST['name']==$username){
					 $log=true;
			 }
			}
		if($log==true){
			$response['error']= true;
			$response['message']= 'email_id/username already used.';
		}
		else{if($_POST['password']==$_POST['conf_password']){
		$stmt = $conn->prepare("INSERT INTO auth (username, email, pass, phno) VALUES (?,?,?,?)");
		$stmt->bind_param("ssss", $_POST['name'],$_POST['email'], $_POST['password'], $_POST['phno']);
		if($stmt->execute()){
		$response['error'] = false;
		$response['message'] = 'registration successfully';
		}else{
		throw new Exception("registration failed");
		}
		}else{
		$response['error'] = true;
		$response['message'] = 'registration failed';
		}
		}
 }
 }catch(Exception $e){
		$response['error'] = true;
		$response['message'] = 'registration failed';
		}
 
 }else{
 $response['error'] = true;
 $response['message'] = "Required params not available";
 }
 
 break;
 
 //in this call we will fetch all the images 
 case 'login':
 
 //query to get images from database
 $userinfo=array();
 if(isset($_POST['username_email'])&&isset($_POST['password'])){
	 $server_ip = gethostbyname(gethostname());
	 $stmt = $conn->prepare("SELECT id, username, email, pass, phno, profilepic  FROM auth");
	 if($stmt->execute()){
		$stmt->bind_result($id, $username, $email, $pass, $phno, $profilepic);
			 //fetching all the images from database
		 //and pushing it to array 
			 while($stmt->fetch()){
				 if($username==$_POST['username_email'] || $email==$_POST['username_email']){
					 if($pass==$_POST['password']){
						 
							$log = true;
							$userinfo['name']=$username;
							$userinfo['email']=$email;
							$userinfo['id']=$id;
							$userinfo['mobile']=$phno;
							if($profilepic!=null){
							$userinfo['profile']='http://'.$server_ip.'/androiduploads/'.UPLOAD_PATH.$profilepic;
							}
							else{
							$userinfo['profile']=null;
							}
							
					 }
				 }
			 }
			 if($log==true){
				$response['error'] = false;
				$response['message'] = "Valid Username and Password";
				$response['userinfo']=$userinfo;
				
			 }else{
				 $response['error'] = true;
			     $response['message'] = "Invalid Username or Password";
			 }
			 
		 }else{
			 $response['error'] = true;
		     $response['message'] = "Unable to Register. Connection Problem";
		 }			 
	 }else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
	 } 
 
 break;


case 'update':

if(isset($_POST['userkey'])&&isset($_POST['change'])&& isset($_POST['id'])){

if(intval($_POST['userkey']) == 1){
$stmt=$conn->prepare('UPDATE auth SET username=? where id=?');
$stmt->bind_param('si',$_POST['change'],intval($_POST['id']));
if($stmt->execute()){
$response['error']=false;
$response['message']='Updated Successfully';
}
else{
$response['error']=true;
$response['message']='Updated Failed';
}

}
else if(intval($_POST['userkey']) == 2){
$stmt=$conn->prepare('UPDATE auth SET email=? where id=?');
$stmt->bind_param('si',$_POST['change'],intval($_POST['id']));
if($stmt->execute()){
$response['error']=false;
$response['message']='Updated Successfully';
}
else{
$response['error']=true;
$response['message']='Updated Failed';
}
}
else if(intval($_POST['userkey']) == 3){
$stmt=$conn->prepare('UPDATE auth SET phno=? where id=?');
$stmt->bind_param('si',$_POST['change'],intval($_POST['id']));
if($stmt->execute()){
$response['error']=false;
$response['message']='Updated Successfully';
}
else{
$response['error']=true;
$response['message']='Updated Failed';
}
}
}
else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
	 } 
break;

case 'profileimg':

if(isset($_FILES['profile_pic']['name'])&&isset($_POST['id'])){
	move_uploaded_file($_FILES['profile_pic']['tmp_name'], UPLOAD_PATH . $_FILES['profile_pic']['name']);
		$stmt = $conn->prepare('UPDATE auth SET profilepic=? where id=?');
		$stmt->bind_param("si", $_FILES['profile_pic']['name'],intval($_POST['id']));
		if($stmt->execute()){
		$response['error'] = false;
		$response['message'] = 'updated successfully';
		}else{
        $response['error'] = true;
        $response['message'] = "Updated Failed";
		}

}else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
	 } 

break;

case 'deleteprofile':
  
 //query to get images from database
 if(isset($_POST['id'])){
		$stmt = $conn->prepare("UPDATE auth SET profilepic=NULL WHERE id=?");
		$stmt->bind_param('i',intval($_POST['id']));
		if($stmt->execute()){
		$response['error'] = false;
		$response['message'] = 'deleted successfully';
		}else{
        $response['error'] = true;
        $response['message'] = "deletion Failed";
		}

}else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
	}
 
 break;

case 'resetpassword':

if(isset($_POST['email'])){
	 $stmt = $conn->prepare("SELECT email FROM auth");
	 if($stmt->execute()){
		$stmt->bind_result($email);
			 //fetching all the images from database
		 //and pushing it to array 
			 while($stmt->fetch()){
				 if($email==$_POST['email']){
					 $log=true;
				 }
			 }
			 if($log==true){
				$response['error'] = false;
				$response['message'] = "Valid Email Address";
				
			 }else{
				 $response['error'] = true;
			     $response['message'] = "Invalid Email Address";
			 }
			 
		 }			 
	 }else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
	 } 
 
 break; 
 
 case 'updatepassword':
  
 if(isset($_POST['pass']) && isset($_POST['email'])){
		$stmt = $conn->prepare("UPDATE auth SET pass=? WHERE email=?");
		$stmt->bind_param('ss',$_POST['pass'],$_POST['email']);
		if($stmt->execute()){
		$response['error'] = false;
		$response['message'] = "updated successfully";
		}else{
        $response['error'] = true;
        $response['message'] = "updation failed";
		}

}else{
		 $response['error'] = true;
		 $response['message'] = "Required params not available";
	}
 
 break;
	 
 
 default: 
 $response['error'] = true;
 $response['message'] = 'Invalid Username or Password';
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