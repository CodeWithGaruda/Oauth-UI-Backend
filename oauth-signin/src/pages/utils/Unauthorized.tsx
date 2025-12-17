import { useNavigate } from "react-router-dom";
import Button from "../../components/ui/button";

const Unauthorized = () => {
  const navigate = useNavigate();

  return (
    <div
      style={{
        height: "100vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
      }}
    >
      <h1>403 – Unauthorized</h1>
      <p>You do not have permission to access this page.</p>

      <button
        style={{
          marginTop: "20px",
          padding: "10px 20px",
          cursor: "pointer",
        }}
        onClick={() => navigate("/login")}
      >
        Go to Login
      </button>
    </div>
  );
};

export default Unauthorized;
