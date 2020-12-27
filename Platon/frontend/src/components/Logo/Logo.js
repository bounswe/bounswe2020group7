import React from 'react';
const logo = (props) => {
    return (
        //<svg fill={props.fill} height={props.height} width={props.width} id="katman_1" data-name="katman 1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 4158.64 736.98"><path d="M3573.67,369.83q0-153.63,0-307.26c0-17,2.48-33,16.79-44.59C3613-.18,3642.14,4.4,3661.3,29.77q112.89,149.42,225.42,299.09,58.41,77.56,116.75,155.19c9.85,13.09,18,29,39.27,21.8,21.5-7.29,20-25.06,20-42.48q-.1-195.25,0-390.51c0-9.22,0-18.59,1.57-27.63,3.86-22.7,22.86-37.79,46.35-37.83,23.26-.05,42.32,15.27,46.25,38,1.45,8.4,1.62,17.09,1.62,25.65q.13,298.33.07,596.67c0,3.3.05,6.61-.06,9.91q-1.32,40.43-30.65,50.34c-23.11,7.82-42.84.57-59.83-22q-170.37-226.43-340.72-452.87c-7.19-9.56-13.94-19.42-27.55-20.47-19.08-1.46-30.46,10.94-30.53,34.78-.2,67.4-.07,134.79-.08,202.19,0,70,.22,140.08-.14,210.12-.16,30.59-17,49.13-44.27,50.4-31.34,1.47-50.25-15.35-50.88-47.09-.75-37.65-.25-75.32-.25-113Q3573.64,469.93,3573.67,369.83Z" transform="translate(0 0)"/><path d="M2272,404.49c0,92.52.19,185-.13,277.57-.09,25.55-16.3,44.44-38.93,47.64-24.69,3.49-46.44-8.56-54.27-29.91-3.53-9.61-2.58-19.58-2.58-29.43Q2176,459.19,2176,248c0-38.33.13-76.67-.06-115-.13-27.16-9.72-36.95-37.15-37.11-52.9-.3-105.79.31-158.68-.28-32.07-.35-49.32-16.66-49.37-44.49,0-23.76,16.06-41.43,39.75-43.51,4-.34,7.93-.41,11.89-.41,160.66,0,321.33.72,482-.53,42-.32,57.43,33.57,49.61,58.54-6,19.24-21.9,30.25-48,30.46-50.24.4-100.49.08-150.74.15-35.42,0-43.17,7.8-43.18,42.94q0,132.84,0,265.69Z" transform="translate(0 0)"/><path d="M664.28,369q0-155.65-.07-311.3c0-20.38,7.78-36,26.14-45.27,16.8-8.47,33.55-6.71,48.83,3.8,17,11.69,20.72,29.23,20.69,48.76q-.3,241.9-.11,483.81c0,17.18-.12,34.37,0,51.55.23,26.07,8,34,34.61,34.16,50.91.25,101.82.08,152.73.08,48.93,0,97.87-.33,146.79.18,24.33.25,41.49,13.55,47.47,34.69,6,21.36-1.4,41-19.75,53.66-8.69,6-18.28,7.27-28.29,7.28-126.95,0-253.9.24-380.85-.16-32.07-.1-48-17.08-48.13-49.94C664.12,576.5,664.28,472.73,664.28,369Z" transform="translate(0 0)"/><path d="M2628.1,558.45c-.47-19.15,4.27-37.2,16.39-51.79,8.86-10.66,8.74-20.64,5.94-32.91-15.42-67.66-9.21-133.69,15.32-198.43,5.32-14.06,10.15-18.46,25-9.29,36.12,22.27,36.66,21.52,26.13,61.35A286.72,286.72,0,0,0,2711.64,451c1.83,10.6,5.51,15.44,16.94,17.53,63.31,11.56,93.3,61.95,73.18,123.1-3.77,11.45,0,16.87,8,23.18,36.54,28.75,77.5,47.82,123.33,55.41,11.75,2,15.8,6.59,15.37,17.92-2.06,54.84,11.61,55.31-51.66,40.65-51-11.83-96.42-36-135.79-70.51-10.22-9-19.8-12.24-33.61-11.07C2672.13,651.92,2628.43,612.15,2628.1,558.45Z" transform="translate(0 0)"/><path d="M3053.75,143.21c-38.59,48-98.82,51.16-140.72,6.14-8.74-9.39-15.65-9.29-25.71-5.62-44.4,16.16-82.33,41.88-112.93,77.73-8.63,10.12-15.57,12.32-26,3.92-5.11-4.09-11.27-6.86-16.92-10.27-25.29-15.28-25.21-15.22-4.81-38.22a333.31,333.31,0,0,1,149-97.54c11-3.56,17-9.08,21.08-20.38,13.3-37,46.89-59.46,85.36-59,37.69.49,70,22.81,83.11,59.32,4.1,11.43,10.87,16.38,21.63,19.91,66.55,21.84,120.83,61.1,164,116.16,8.23,10.49,9.22,16.11-4.22,23.49-39.25,21.53-38.88,22.1-69.3-9.08-29.54-30.28-64.62-52-104-67.12C3066.71,140.07,3060.1,136.71,3053.75,143.21Z" transform="translate(0 0)"/><path d="M3319.92,397.94a357.43,357.43,0,0,1-9.29,81.26c-2.14,9.32-.74,15.9,5,23.51,23.79,31.67,26.75,65.87,7,100.14s-51.17,48.82-90.29,44.18c-12.55-1.49-20.9,2-30,10-49,42.72-106.18,68.27-170.33,78.82-13.49,2.22-18.72-.43-18.76-15.46-.12-44.43-.65-44.11,41.75-55.93,36-10,68.59-27.16,98.13-50.24,9.2-7.18,10.88-13.52,6.86-25.1-19.17-55.21,11.85-109.23,69.29-119.48,15.94-2.84,20.25-10,22.41-24,6.88-44.65,3.84-88.41-11.16-130.89-4.58-13-1.2-18.56,10.11-24.68,44.55-24.13,44.46-24.52,57.8,24.57a245.61,245.61,0,0,1,7,32.86C3317.64,365.76,3321,384.05,3319.92,397.94Z" transform="translate(0 0)"/><path d="M1887.79,664.37c-2-5.6-4.26-11.08-6.55-16.56q-73.47-175.27-147-350.55-52.82-126-105.69-251.9c-7.95-19-17.77-35.56-41.34-37.57-26.94-2.3-41.63,7.26-53.94,36.53Q1473,187.57,1413,330.9,1345.14,492.43,1277.26,654c-8,19.12-12,38.37.67,56.67,21.89,31.7,68,25.48,84.41-11.84,14.84-33.82,28.37-68.25,41.59-102.75,6.84-17.85,18.14-25.32,37.52-25.19q138.87.93,277.74,0c20.43-.15,31.55,8.16,38.59,26.66,13.15,34.52,27.72,68.5,41.53,102.77,7.65,19,20.76,30.09,41.87,30.23C1878.78,730.59,1900.28,700.15,1887.79,664.37ZM1668.46,491.66c-29,.47-58.06.11-87.09.11s-58.07.25-87.09-.06c-29.25-.32-40.67-17.13-29.61-44.2,29.64-72.53,59.77-144.86,89.52-217.34,5.16-12.55,12.25-21.44,27.31-21.52s22.48,8.44,27.56,21.12c29.36,73.35,59.1,146.55,88.22,220C1707.27,475,1695.87,491.22,1668.46,491.66Z" transform="translate(0 0)"/><path d="M470.68,228.09C464.9,105,369.6,10.3,246.58,7.77c-61.47-1.27-123-.51-184.49-.53-46.7,0-62,15.13-62,61.51Q-.08,218.47,0,368.19q0,150.7,0,301.42c0,14.51-.41,29.17,9.26,41.67a50.49,50.49,0,0,0,55.14,17.18c19.67-6.37,32-23.34,32.27-47.55.53-54.86.13-109.73.26-164.59.06-27.72,8.74-36.24,37.06-36.39,32.4-.16,64.81.2,97.21-.1,59.14-.56,114.36-14.62,160.28-53.42C452.75,374.68,474.36,306.49,470.68,228.09ZM249.61,389c-40.24,2-80.62,1.57-120.93,1.52-21.82,0-31.41-9.85-31.61-31.62-.37-38.33-.11-76.66-.11-115s-.17-76.67.05-115c.13-20.86,9-32.45,28.8-32.54,44.89-.2,89.93-3.76,134.67,2.62,71.73,10.24,121.14,75.18,118.21,154.93C376,328.5,322.43,385.36,249.61,389Z" transform="translate(0 0)"/></svg>
        <svg
        id="prefix__katman_1"
        data-name="katman 1"
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 4534.15 1043.05"
        height={props.height} width={props.width}
      >
        <defs>
          <style>
            {
              ".prefix__cls-1{fill:#e0dfd5}.prefix__cls-4{fill:#ededed}.prefix__cls-5{fill:#ddd}"
            }
          </style>
        </defs>
        <path
          className="prefix__cls-1"
          d="M3573.67 369.83V62.57c0-17 2.48-33 16.79-44.59C3613-.18 3642.14 4.4 3661.3 29.77q112.89 149.42 225.42 299.09 58.41 77.56 116.75 155.19c9.85 13.09 18 29 39.27 21.8 21.5-7.29 20-25.06 20-42.48q-.1-195.25 0-390.51c0-9.22 0-18.59 1.57-27.63 3.86-22.7 22.86-37.79 46.35-37.83 23.26-.05 42.32 15.27 46.25 38 1.45 8.4 1.62 17.09 1.62 25.65q.14 298.33.07 596.67c0 3.3 0 6.61-.06 9.91q-1.32 40.43-30.65 50.34c-23.11 7.82-42.84.57-59.83-22q-170.37-226.43-340.72-452.87c-7.19-9.56-13.94-19.42-27.55-20.47-19.08-1.46-30.46 10.94-30.53 34.78-.2 67.4-.07 134.79-.08 202.19 0 70 .22 140.08-.14 210.12-.16 30.59-17 49.13-44.27 50.4-31.34 1.47-50.25-15.35-50.88-47.09-.75-37.65-.25-75.32-.25-113q0-100.09.03-200.2zM2272 404.49c0 92.52.19 185-.13 277.57-.09 25.55-16.3 44.44-38.93 47.64-24.69 3.49-46.44-8.56-54.27-29.91-3.53-9.61-2.58-19.58-2.58-29.43Q2176 459.19 2176 248c0-38.33.13-76.67-.06-115-.13-27.16-9.72-36.95-37.15-37.11-52.9-.3-105.79.31-158.68-.28-32.07-.35-49.32-16.66-49.37-44.49 0-23.76 16.06-41.43 39.75-43.51 4-.34 7.93-.41 11.89-.41 160.66 0 321.33.72 482-.53 42-.32 57.43 33.57 49.61 58.54-6 19.24-21.9 30.25-48 30.46-50.24.4-100.49.08-150.74.15-35.42 0-43.17 7.8-43.18 42.94v265.69zM664.28 369q0-155.65-.07-311.3c0-20.38 7.78-36 26.14-45.27 16.8-8.47 33.55-6.71 48.83 3.8 17 11.69 20.72 29.23 20.69 48.76q-.3 241.9-.11 483.81c0 17.18-.12 34.37 0 51.55.23 26.07 8 34 34.61 34.16 50.91.25 101.82.08 152.73.08 48.93 0 97.87-.33 146.79.18 24.33.25 41.49 13.55 47.47 34.69 6 21.36-1.4 41-19.75 53.66-8.69 6-18.28 7.27-28.29 7.28-126.95 0-253.9.24-380.85-.16-32.07-.1-48-17.08-48.13-49.94-.22-103.8-.06-207.57-.06-311.3zM2628.1 558.45c-.47-19.15 4.27-37.2 16.39-51.79 8.86-10.66 8.74-20.64 5.94-32.91-15.42-67.66-9.21-133.69 15.32-198.43 5.32-14.06 10.15-18.46 25-9.29 36.12 22.27 36.66 21.52 26.13 61.35a286.78 286.78 0 00-5.24 123.62c1.83 10.6 5.51 15.44 16.94 17.53 63.31 11.56 93.3 62 73.18 123.1-3.77 11.45 0 16.87 8 23.18 36.54 28.75 77.5 47.82 123.33 55.41 11.75 2 15.8 6.59 15.37 17.92-2.06 54.84 11.61 55.31-51.66 40.65-51-11.83-96.42-36-135.79-70.51-10.22-9-19.8-12.24-33.61-11.07-55.27 4.71-98.97-35.06-99.3-88.76zM3053.75 143.21c-38.59 48-98.82 51.16-140.72 6.14-8.74-9.39-15.65-9.29-25.71-5.62-44.4 16.16-82.33 41.88-112.93 77.73-8.63 10.12-15.57 12.32-26 3.92-5.11-4.09-11.27-6.86-16.92-10.27-25.29-15.28-25.21-15.22-4.81-38.22a333.31 333.31 0 01149-97.54c11-3.56 17-9.08 21.08-20.38C2910 22 2943.63-.49 2982.1 0c37.69.49 70 22.81 83.11 59.32 4.1 11.43 10.87 16.38 21.63 19.91 66.55 21.84 120.83 61.1 164 116.16 8.23 10.49 9.22 16.11-4.22 23.49-39.25 21.53-38.88 22.1-69.3-9.08-29.54-30.28-64.62-52-104-67.12-6.61-2.61-13.22-5.97-19.57.53zM3319.92 397.94a357.35 357.35 0 01-9.29 81.26c-2.14 9.32-.74 15.9 5 23.51 23.79 31.67 26.75 65.87 7 100.14s-51.17 48.82-90.29 44.18c-12.55-1.49-20.9 2-30 10-49 42.72-106.18 68.27-170.33 78.82-13.49 2.22-18.72-.43-18.76-15.46-.12-44.43-.65-44.11 41.75-55.93 36-10 68.59-27.16 98.13-50.24 9.2-7.18 10.88-13.52 6.86-25.1-19.17-55.21 11.85-109.23 69.29-119.48 15.94-2.84 20.25-10 22.41-24 6.88-44.65 3.84-88.41-11.16-130.89-4.58-13-1.2-18.56 10.11-24.68 44.55-24.13 44.46-24.52 57.8 24.57a245.61 245.61 0 017 32.86c2.2 18.26 5.56 36.55 4.48 50.44zM1887.79 664.37c-2-5.6-4.26-11.08-6.55-16.56q-73.47-175.27-147-350.55-52.82-126-105.69-251.9c-8-19-17.77-35.56-41.34-37.57-26.94-2.3-41.63 7.26-53.94 36.53Q1473 187.57 1413 330.9L1277.26 654c-8 19.12-12 38.37.67 56.67 21.89 31.7 68 25.48 84.41-11.84 14.84-33.82 28.37-68.25 41.59-102.75 6.84-17.85 18.14-25.32 37.52-25.19q138.87.93 277.74 0c20.43-.15 31.55 8.16 38.59 26.66 13.15 34.52 27.72 68.5 41.53 102.77 7.65 19 20.76 30.09 41.87 30.23 37.6.04 59.1-30.4 46.61-66.18zm-219.33-172.71c-29 .47-58.06.11-87.09.11s-58.07.25-87.09-.06c-29.25-.32-40.67-17.13-29.61-44.2 29.64-72.53 59.77-144.86 89.52-217.34 5.16-12.55 12.25-21.44 27.31-21.52s22.48 8.44 27.56 21.12c29.36 73.35 59.1 146.55 88.22 220 9.99 25.23-1.41 41.45-28.82 41.89zM470.68 228.09C464.9 105 369.6 10.3 246.58 7.77c-61.47-1.27-123-.51-184.49-.53-46.7 0-62 15.13-62 61.51Q-.08 218.47 0 368.19v301.42c0 14.51-.41 29.17 9.26 41.67a50.49 50.49 0 0055.14 17.18c19.67-6.37 32-23.34 32.27-47.55.53-54.86.13-109.73.26-164.59.06-27.72 8.74-36.24 37.06-36.39 32.4-.16 64.81.2 97.21-.1 59.14-.56 114.36-14.62 160.28-53.42 61.27-51.73 82.88-119.92 79.2-198.32zM249.61 389c-40.24 2-80.62 1.57-120.93 1.52-21.82 0-31.41-9.85-31.61-31.62-.37-38.33-.11-76.66-.11-115s-.17-76.67.05-115c.13-20.86 9-32.45 28.8-32.54 44.89-.2 89.93-3.76 134.67 2.62 71.73 10.24 121.14 75.18 118.21 154.93C376 328.5 322.43 385.36 249.61 389z"
          transform="translate(375.53 306.1)"
        />
        <g id="prefix___1_hat" data-name="1 hat">
          <g id="prefix__fabric">
            <path
              d="M-279 145.08c1.09-3.06 73.32-55 76-56.73l118.6-77.3A308.41 308.41 0 00-65.19 193L408.42-6c-21.19-44.16-86.71-130.72-114.73-170.56-8.2-11.65-16.76-23.19-27.35-32.54-12-10.6-26.29-18-40.5-25.14a1446.14 1446.14 0 00-141.41-61.44c-16.32-6.07-33.38-11.94-50.65-10.07-15 1.63-28.89 9-42.33 16.25-24.73 13.34-24.51 13.33-49.24 26.66-6.8 3.66-35.92 21.72-41.5 27.16-5.11 5-15.71 17.25-19.46 23.4C-168.19-131.27-213.6-57.22-258 26.88c-2.25 4.28-41.64 71.15-42 76"
              transform="translate(375.53 306.1)"
              fill="#ed2f2f"
            />
            <path
              d="M-26.61 136.79c3.33 12 6.67 21.82 9.87 28.5L-65.19 193A308.41 308.41 0 01-84.4 11.05l-118.66 77.3c-2.67 1.74-78.62 57.44-78.62 57.44.08-.21 64.84-59.19 65-59.27 74.36-58.23 104.8-70.08 140.28-108.34l17.6-72.79a.84.84 0 011.66.23C-59.6-54-50 26.59-37.62 89.16"
              transform="translate(375.53 306.1)"
              fill="#dd281f"
            />
          </g>
          <g id="prefix__fur_ball" data-name="fur ball">
            <path
              className="prefix__cls-4"
              d="M-324.06 204.35a61.65 61.65 0 0029.35 16c-.3-3.67-.61-7.34-.91-11a41 41 0 0135.95 11.19c7.78-12.33 15.69-24.95 19-39.15s1.31-30.5-8.8-41a27.5 27.5 0 0119.32 10.39 46 46 0 00-12.75-34.86 46 46 0 00-34.5-13.68 52.35 52.35 0 0116.06-13.46 55.29 55.29 0 00-31.77 5.1 80.75 80.75 0 00-17.65-15.44q-1 10-2.06 20.12a62.14 62.14 0 00-56.29 14.33l13.22 6.82c-21.42 19.16-26.08 54.41-10.38 78.48a43.75 43.75 0 019.66-13.69 339.91 339.91 0 0026.74 31.71l5-11.83"
              transform="translate(375.53 306.1)"
            />
            <path
              className="prefix__cls-5"
              d="M-240.64 181.41c3.33-14.19 1.31-30.5-8.8-41a27.5 27.5 0 0119.32 10.39 46 46 0 00-12.75-34.86 46 46 0 00-25.43-13.12 149.1 149.1 0 0115.82 20.13A124.56 124.56 0 00-273 122l11.14 16.89a9 9 0 00-6.43 1.83c7.26 7.77 10.47 18.68 10.62 29.31s-2.52 21.11-5.73 31.24c-11.7-10.24-29.21-8.9-44.64-7q.48 4.44.94 8.88a130.54 130.54 0 00-27.74-12.83 56.33 56.33 0 005.92 23.68q2-4.82 4.06-9.64h.8a61.65 61.65 0 0029.35 16c-.3-3.67-.61-7.34-.91-11a41 41 0 0135.95 11.2c7.78-12.33 15.67-24.95 19.03-39.15z"
              transform="translate(375.53 306.1)"
            />
          </g>
          <g id="prefix__fur">
            <path
              className="prefix__cls-4"
              d="M393.31-63a50 50 0 013.09 25.23c-20.25-18.66-45.08-31.71-71-37.29q-1.07 10.06-2.16 20.18C217.75-95.8 92.88-52.59 37.76 43.9L29 19.21C25.62 40.06 9.5 55.32-7.51 66.06S-43.48 84.75-58 98.78c-26.74 25.73-34.14 67.56-32.58 106.83l21.54-17.89c-2.07 10.55-6.57 21.29-7.33 32.08-.88 12.44 3.4 24.89 10.35 35.25s16.43 18.78 26.57 26.05c-3.53-5.46-2.61-12.61-1-18.91.32-1.26.72-2.51 1.08-3.76a101.83 101.83 0 0060.05 5.08c33.31-7.33 61.55-28.12 88.85-48.46l66.28-49.41c-7.51 9-11.15 21.5-9.87 33.83 33.32-27.68 66.93-55.56 105.37-75.16s82.53-30.59 126.1-22.54a23 23 0 01-10.62 15.7c37.32 4.42 82.21-29.17 81-71.88a101.17 101.17 0 00-10.14-41.66C444-25.58 423.29-39 393.31-63z"
              transform="translate(375.53 306.1)"
            />
            <path
              className="prefix__cls-5"
              d="M396.4-37.78A153.89 153.89 0 00329.5-74c63 37.14 83.47 66.41 92 84.86 6 12.91 2.39 42.66-10.44 48.8-1.25-11.07-3-18.19-9.23-27.46-4.29 17.89-21.12 30.31-38.61 36s-36.15 6.29-54.22 9.6A181.39 181.39 0 00209 133a78.89 78.89 0 0119.52-37.46c-45.9 25-78.05 68.58-114.51 106-24.08 24.73-52.93 47.85-84.74 59.59 29.58-8.75 55.3-27.55 80.19-46.09l66.28-49.42c-7.51 9-11.15 21.51-9.87 33.84 33.32-27.68 66.93-55.56 105.37-75.16s82.53-30.59 126.1-22.54a23 23 0 01-10.62 15.7c37.32 4.42 82.21-29.17 81-71.88a101.17 101.17 0 00-10.07-41.65C444-25.58 423.29-39 393.31-63a50 50 0 013.09 25.22z"
              transform="translate(375.53 306.1)"
            />
          </g>
        </g>
      </svg>
    );
}

export default logo;