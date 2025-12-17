import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider } from "./auth/AuthContext";
import SignIn from "./auth/signin";
import Home from "./pages/home";
import PrivateRoute from "./routes/PrivateRoute";
import Unauthorized from "./pages/utils/Unauthorized";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<SignIn />} />
          <Route path="/unauthorized" element={<Unauthorized />} />

          <Route
            path="/home"
            element={
              <PrivateRoute allowedRoles={["MEMBER", "ADMIN"]}>
                <Home />
              </PrivateRoute>
            }
          />

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
