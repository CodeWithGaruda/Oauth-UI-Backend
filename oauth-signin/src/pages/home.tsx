import React, { useEffect, useState } from "react";

interface User {
  name: string;
  email: string;
  phoneNumber?: string;
  role?: string;
  pictureUrl?: string;
}

const Home: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchUser = async (): Promise<void> => {
      try {
        const res = await fetch("http://localhost:8080/api/user", {
          credentials: "include",
        });

        if (!res.ok) {
          setUser(null);
        } else {
          const data: User = await res.json();
          setUser(data);
        }
      } catch (error) {
        console.error("Fetch /api/home failed", error);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  if (loading) {
    return <div style={{ padding: 20 }}>Loading...</div>;
  }

  if (!user) {
    return (
      <div style={{ padding: 20 }}>
        Not authenticated. Please <a href="/login">login</a>.
      </div>
    );
  }

  return (
    <div style={{ padding: 20 }}>
      <h1>Welcome, {user.name}</h1>

      <p>
        <strong>Email:</strong> {user.email}
      </p>

      <p>
        <strong>Phone:</strong> {user.phoneNumber ?? "Not available"}
      </p>

      <p>
        <strong>Role:</strong> {user.role ?? "Not available"}
      </p>

      {user.pictureUrl && (
        <img
          src={user.pictureUrl}
          alt="Profile"
          style={{
            width: 120,
            height: 120,
            borderRadius: "50%",
            marginTop: 20,
          }}
        />
      )}
    </div>
  );
};

export default Home;
