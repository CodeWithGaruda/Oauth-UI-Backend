import { GoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";

function SignIn() {
  const navigate = useNavigate();

  return (
    <div style={{ textAlign: "center", marginTop: "100px" }}>
      <h2>Login with Google</h2>

      <GoogleLogin
        // src/Login.js (only the onSuccess part)
        onSuccess={async (cred) => {
          const idToken = cred?.credential;
          console.log("ID Token:", idToken);

          if (!idToken) {
            alert("No id token returned");
            return;
          }

          try {
            const res = await fetch("http://localhost:8080/api/auth/google", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              credentials: "include", // important to receive session cookie
              body: JSON.stringify({ id_token: idToken }),
            });

            if (res.ok) {
              // authenticated on backend; now go to /home which will fetch user info
              navigate("/home");
            } else {
              const text = await res.text();
              alert("Backend auth failed: " + text);
              console.error("Backend response:", text);
            }
          } catch (err) {
            console.error("Network or CORS error:", err);
            alert("Could not reach backend. Check console/Network tab.");
          }
        }}
        onError={() => {
          alert("Login Failed");
        }}
      />
      {/* <StartPeopleAuthButton /> */}
    </div>
  );
}

export default SignIn;
