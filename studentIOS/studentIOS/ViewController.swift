//
//  ViewController.swift
//  studentIOS
//
//  Created by Ming Yue on 2018-04-29.
//  Copyright © 2018 CS5999Project. All rights reserved.
//

import UIKit
import WebKit
class ViewController: UIViewController {

    @IBOutlet weak var studentWebView: WKWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //TODO: change this to proper URL after we have a server
        let url = URL(string: "http://192.168.0.5:8080/student/")
        
        
        let request = URLRequest(url:url!)
        studentWebView.load(request)
    }

    override var prefersStatusBarHidden: Bool{
        return true
    }
    
}

