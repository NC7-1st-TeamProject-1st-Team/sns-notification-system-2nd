import { Route, Routes } from 'react-router-dom';
import './App.css';
import { Helmet } from 'react-helmet-async';
import ExamplePage from './pages/ExamplePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ChatPage from './pages/ChatPage';

function App() {
  return (
    <>
      <Helmet>
        <title>BitSNS</title>
      </Helmet>
      <Routes>
        <Route element={<ExamplePage />} path={'/'} />
        <Route element={<LoginPage />} path={'/auth/login'} />
        <Route element={<RegisterPage />} path={'/auth/register'} />
        <Route element={<ChatPage />} path={'/chat'} />
      </Routes>
    </>
  );
}

export default App;
