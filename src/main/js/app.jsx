import React from 'react';
import {render} from 'react-dom';
import Tree from './components/Tree.jsx';
import styles from '../stylesheets/main.scss';
render(
    <Tree/>,
    document.getElementById('react_container')
);