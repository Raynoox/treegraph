import React from 'react';
import {render} from 'react-dom';
import Tree from './components/Tree.jsx';
import Alert from 'react-s-alert';
import styles from '../stylesheets/main.scss';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';

render(
    <div>
    <Tree/>
        <Alert stack={{limit: 3}} />
    </div>,
    document.getElementById('react_container')
);