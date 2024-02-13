-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 13-02-2024 a las 13:04:26
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `decantalo`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `production`
--

CREATE TABLE `production` (
  `ID` int(10) UNSIGNED ZEROFILL NOT NULL,
  `TimeStamp` datetime DEFAULT current_timestamp(),
  `OriginalTarget` tinytext DEFAULT NULL,
  `Target` tinytext DEFAULT NULL,
  `OutputChute` tinytext DEFAULT NULL,
  `nProcess` smallint(6) DEFAULT NULL,
  `_Order` tinytext DEFAULT NULL,
  `Code_1` tinytext DEFAULT NULL,
  `Code_2` tinytext DEFAULT NULL,
  `Code_3` tinytext DEFAULT NULL,
  `Code_4` tinytext DEFAULT NULL,
  `_Lenght` float DEFAULT 0,
  `_Heigt` float DEFAULT 0,
  `_With` float DEFAULT 0,
  `_Weight` float DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

--
-- Volcado de datos para la tabla `production`
--

INSERT INTO `production` (`ID`, `TimeStamp`, `OriginalTarget`, `Target`, `OutputChute`, `nProcess`, `_Order`, `Code_1`, `Code_2`, `Code_3`, `Code_4`, `_Lenght`, `_Heigt`, `_With`, `_Weight`) VALUES
(0100000034, '2024-02-13 12:16:38', '0', NULL, NULL, 2, NULL, 'Zdeoid', NULL, NULL, NULL, 0, 0, 0, 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `production`
--
ALTER TABLE `production`
  ADD PRIMARY KEY (`ID`) USING BTREE;

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `production`
--
ALTER TABLE `production`
  MODIFY `ID` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=100000035;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
