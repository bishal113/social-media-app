<?php
require 'PHPMailerAutoload.php';
	if(isset($_POST['otp']) && isset($_POST['to_mail']))
	{
		// Fetching data that is entered by the user
		$email = "cu.16bcs1190@gmail.com";
		$password = "Mukesh@98";
		$to_id = $_POST['to_mail']; 
		//$_POST['toid'];
		$message = $_POST['otp'];
		//$_POST['message'];
		$subject = "OTP";
		//$_POST['subject']
		// Configuring SMTP server settings
		$mail = new PHPMailer;
		$mail->isSMTP();
		$mail->Host = 'smtp.gmail.com';
		$mail->Port = 587;
		$mail->SMTPSecure = 'tls';
		$mail->SMTPAuth = true;
		$mail->Username = $email;
		$mail->Password = $password;

		// Email Sending Details
		$mail->addAddress($to_id);
		$mail->Subject = $subject;
		$mail->msgHTML($message);

		$response = array();
		// Success or Failure
		if (!$mail->send()) {
		$error = "Mailer Error: " . $mail->ErrorInfo;
		$response['error'] = true;
		$response['message'] = "Could not send otp";
		}
		else {
		$response['error'] = false;
		$response['message'] = "OTP sent";
		}
	}else{
		$response['error'] = true;
		$response['message'] = "Required params not available";
	}
	header('Content-Type: application/json');
	echo json_encode($response);
?>

